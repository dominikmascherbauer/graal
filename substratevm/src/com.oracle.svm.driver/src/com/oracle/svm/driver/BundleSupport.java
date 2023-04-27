/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.svm.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.oracle.svm.core.util.ExitStatus;
import org.graalvm.collections.EconomicMap;
import org.graalvm.util.json.JSONParser;
import org.graalvm.util.json.JSONParserException;

import com.oracle.svm.core.OS;
import com.oracle.svm.core.SubstrateUtil;
import com.oracle.svm.core.configure.ConfigurationParser;
import com.oracle.svm.core.option.BundleMember;
import com.oracle.svm.core.util.json.JsonPrinter;
import com.oracle.svm.core.util.json.JsonWriter;
import com.oracle.svm.util.ClassUtil;

final class BundleSupport {

    final NativeImage nativeImage;

    final Path rootDir;
    final Path inputDir;
    final Path stageDir;
    final Path classPathDir;
    final Path modulePathDir;
    final Path auxiliaryDir;
    final Path outputDir;
    final Path imagePathOutputDir;
    final Path auxiliaryOutputDir;

    Map<Path, Path> pathCanonicalizations = new HashMap<>();
    Map<Path, Path> pathSubstitutions = new HashMap<>();

    private final boolean forceBuilderOnClasspath;
    private final List<String> nativeImageArgs;
    private List<String> updatedNativeImageArgs;

    boolean loadBundle;
    boolean writeBundle;

    private static final int BUNDLE_FILE_FORMAT_VERSION_MAJOR = 0;
    private static final int BUNDLE_FILE_FORMAT_VERSION_MINOR = 9;

    private static final String BUNDLE_INFO_MESSAGE_PREFIX = "Native Image Bundles: ";
    private static final String BUNDLE_TEMP_DIR_PREFIX = "bundleRoot-";
    private static final String ORIGINAL_DIR_EXTENSION = ".orig";

    private Path bundlePath;
    private String bundleName;

    private final BundleProperties bundleProperties;

    static final String BUNDLE_OPTION = "--bundle";
    static final String BUNDLE_FILE_EXTENSION = ".nib";

    final Path containerGraalVMHome = Path.of("/graalvm");
    boolean useContainer;
    private String containerTool;
    private String bundleContainerTool;
    private String containerToolVersion;
    private String bundleContainerToolVersion;
    private String containerImage;
    private String bundleContainerImage;
    private Path dockerfile;
    private Path bundleDockerfile;
    private static final List<String> SUPPORTED_CONTAINER_TOOLS = List.of("podman", "docker");
    private static final String DEFAULT_DOCKERFILE = "FROM registry.fedoraproject.org/fedora-minimal:latest" + System.lineSeparator() +
            "RUN microdnf -y install gcc g++ zlib-static --nodocs --setopt install_weak_deps=0 && microdnf clean all -y";
    private final String containerToolJsonKey = "containerTool";
    private final String containerToolVersionJsonKey = "containerToolVersion";
    private final String containerImageJsonKey = "containerImage";


    enum BundleOptionVariants {
        create(),
        apply();

        String optionName() {
            return BUNDLE_OPTION + "-" + this;
        }
    }

    enum ExtendedBundleOptions {
        dry_run,
        container,
        dockerfile;

        static ExtendedBundleOptions get(String name) {
            return ExtendedBundleOptions.valueOf(name.replace('-', '_'));
        }

        @Override
        public String toString() {
            return super.toString().replace('_', '-');
        }
    }

    static BundleSupport create(NativeImage nativeImage, String bundleArg, NativeImage.ArgumentQueue args) {
        try {
            String variant = bundleArg.substring(BUNDLE_OPTION.length() + 1);
            String bundleFilename = null;
            String[] options = SubstrateUtil.split(variant, ",");

            variant = options[0];
            String[] variantParts = SubstrateUtil.split(variant, "=", 2);
            if (variantParts.length == 2) {
                variant = variantParts[0];
                bundleFilename = variantParts[1];
            }
            String applyOptionName = BundleOptionVariants.apply.optionName();
            String createOptionName = BundleOptionVariants.create.optionName();
            BundleSupport bundleSupport;
            switch (BundleOptionVariants.valueOf(variant)) {
                case apply:
                    if (nativeImage.useBundle()) {
                        if (nativeImage.bundleSupport.loadBundle) {
                            throw NativeImage.showError(String.format("native-image allows option %s to be specified only once.", applyOptionName));
                        }
                        if (nativeImage.bundleSupport.writeBundle) {
                            throw NativeImage.showError(String.format("native-image option %s is not allowed to be used after option %s.", applyOptionName, createOptionName));
                        }
                    }
                    if (bundleFilename == null) {
                        throw NativeImage.showError(String.format("native-image option %s requires a bundle file argument. E.g. %s=bundle-file.nib.", applyOptionName, applyOptionName));
                    }
                    bundleSupport = new BundleSupport(nativeImage, bundleFilename);
                    /* Inject the command line args from the loaded bundle in-place */
                    List<String> buildArgs = bundleSupport.getNativeImageArgs();
                    for (int i = buildArgs.size() - 1; i >= 0; i--) {
                        args.push(buildArgs.get(i));
                    }
                    nativeImage.showVerboseMessage(nativeImage.isVerbose(), BUNDLE_INFO_MESSAGE_PREFIX + "Inject args: '" + String.join(" ", buildArgs) + "'");
                    /* Snapshot args after in-place expansion (includes also args after this one) */
                    bundleSupport.updatedNativeImageArgs = args.snapshot();
                    break;
                case create:
                    if (nativeImage.useBundle()) {
                        if (nativeImage.bundleSupport.writeBundle) {
                            throw NativeImage.showError(String.format("native-image allows option %s to be specified only once.", bundleArg));
                        } else {
                            bundleSupport = nativeImage.bundleSupport;
                            bundleSupport.writeBundle = true;
                        }
                    } else {
                        bundleSupport = new BundleSupport(nativeImage);
                    }
                    if (bundleFilename != null) {
                        bundleSupport.updateBundleLocation(Path.of(bundleFilename), true);
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            Arrays.stream(options)
                    .skip(1)
                    .forEach(option -> {
                        String optionValue = null;
                        String[] optionParts = SubstrateUtil.split(option, "=", 2);
                        if (optionParts.length == 2) {
                            option = optionParts[0];
                            optionValue = optionParts[1];
                        }
                        switch (ExtendedBundleOptions.get(option)) {
                            case dry_run -> nativeImage.setDryRun(true);
                            case container -> {
                                if (bundleSupport.useContainer) {
                                    throw NativeImage.showError(String.format("native-image bundle allows option %s to be specified only once.", option));
                                }
                                bundleSupport.useContainer = true;
                                if (optionValue != null) {
                                    if (!SUPPORTED_CONTAINER_TOOLS.contains(optionValue)) {
                                        throw NativeImage.showError(String.format("Container Tool '%s' is not supported, please use one of the following tools: %s", optionValue, SUPPORTED_CONTAINER_TOOLS));
                                    }
                                    bundleSupport.containerTool = optionValue;
                                }
                            }
                            case dockerfile -> {
                                if (!bundleSupport.useContainer) {
                                    throw NativeImage.showError(String.format("native-image bundle option %s is only allowed to be used after option %s.", option, ExtendedBundleOptions.container));
                                }
                                if (bundleSupport.dockerfile != null) {
                                    throw NativeImage.showError(String.format("native-image bundle allows option %s to be specified only once.", option));
                                }
                                if (optionValue != null) {
                                    bundleSupport.dockerfile = Path.of(optionValue);
                                    if (!Files.isReadable(bundleSupport.dockerfile)) {
                                        throw NativeImage.showError(String.format("Dockerfile '%s' is not readable", bundleSupport.dockerfile.toAbsolutePath()));
                                    }
                                }
                            }
                            default -> {
                                String suggestedOptions = Arrays.stream(ExtendedBundleOptions.values())
                                        .map(Enum::toString)
                                        .collect(Collectors.joining(", "));
                                throw NativeImage.showError(String.format("Unknown option %s. Valid options are: %s.", option, suggestedOptions));
                            }
                        }
                    });

            if(bundleSupport.useContainer) {
                if (!OS.LINUX.isCurrent()) {
                    nativeImage.showMessage(BUNDLE_INFO_MESSAGE_PREFIX, "Skipping containerized build, only supported for Linux.");
                    bundleSupport.useContainer = false;
                } else {
                    if(nativeImage.isDryRun()) {
                        nativeImage.showMessage(BUNDLE_INFO_MESSAGE_PREFIX + "Skipping container creation for native-image bundle with dry-run option.");
                    } else {
                        bundleSupport.initializeContainerImage();
                    }
                }
            }

            return bundleSupport;

        } catch (StringIndexOutOfBoundsException | IllegalArgumentException e) {
            String suggestedVariants = Arrays.stream(BundleOptionVariants.values())
                            .map(v -> BUNDLE_OPTION + "-" + v)
                            .collect(Collectors.joining(", "));
            throw NativeImage.showError("Unknown option " + bundleArg + ". Valid variants are: " + suggestedVariants + ".");
        }
    }

    private void initializeContainerImage() {
        String bundleFileName = bundlePath.resolve(bundleName + BUNDLE_FILE_EXTENSION).toString();

        if(bundleDockerfile != null && dockerfile == null) {
            dockerfile = bundleDockerfile;
        }

        // create Dockerfile if not available for writing or loading bundle
        try {
            // TODO load graalvm docker base
            if (dockerfile == null) {
                dockerfile = Files.createTempFile("Dockerfile", null);
                Files.write(dockerfile, DEFAULT_DOCKERFILE.getBytes());
                dockerfile.toFile().deleteOnExit();
                containerImage = SubstrateUtil.digest(DEFAULT_DOCKERFILE);
            } else {
                containerImage = SubstrateUtil.digest(Files.readString(dockerfile));
            }
        } catch (IOException e) {
            throw NativeImage.showError(e.getMessage());
        }

        if(bundleContainerImage != null && !bundleContainerImage.equals(containerImage)) {
            NativeImage.showWarning(String.format("The given bundle file %s was created with a different dockerfile.", bundleFileName));
        }

        if(bundleContainerTool != null && containerTool == null) {
            containerTool = bundleContainerTool;
        }

        if(containerTool != null) {
            if(!isToolAvailable(containerTool)) {
                throw NativeImage.showError("Configured container tool not available.");
            } else if(containerTool.equals("docker") && !isRootlessDocker()) {
                throw NativeImage.showError("Only rootless docker is supported for containerized builds.");
            }
            containerToolVersion = getContainerToolVersion(containerTool);

            if(bundleContainerTool != null) {
                if (!containerTool.equals(bundleContainerTool)) {
                    NativeImage.showWarning(String.format("The given bundle file %s was created with container tool '%s' (using '%s').", bundleFileName, bundleContainerTool, containerTool));
                } else if (containerToolVersion != null && bundleContainerToolVersion != null && !containerToolVersion.equals(bundleContainerToolVersion)) {
                    NativeImage.showWarning(String.format("The given bundle file %s was created with different %s version '%s' (installed '%s').", bundleFileName, containerTool, bundleContainerToolVersion, containerToolVersion));
                }
            }
        } else {
            for(String tool : SUPPORTED_CONTAINER_TOOLS) {
                if(isToolAvailable(tool)) {
                    if(tool.equals("docker") && !isRootlessDocker()) {
                        nativeImage.showMessage(BUNDLE_INFO_MESSAGE_PREFIX + "Rootless context missing for docker.");
                        continue;
                    }
                    containerTool = tool;
                    containerToolVersion = getContainerToolVersion(tool);
                    break;
                }
            }
            if (containerTool == null) {
                throw NativeImage.showError(String.format("Please install one of the following tools before running containerized native image builds: %s", SUPPORTED_CONTAINER_TOOLS));
            }
        }

        int exitStatusCode = createContainer();
        switch (ExitStatus.of(exitStatusCode)) {
            case OK:
                break;
            case BUILDER_ERROR:
                /* Exit, builder has handled error reporting. */
                throw NativeImage.showError(null, null, exitStatusCode);
            case OUT_OF_MEMORY:
                nativeImage.showOutOfMemoryWarning();
                throw NativeImage.showError(null, null, exitStatusCode);
            default:
                String message = String.format("Container build request for '%s' failed with exit status %d",
                        nativeImage.imageName, exitStatusCode);
                throw NativeImage.showError(message, null, exitStatusCode);
        }
    }

    private int createContainer() {
        ProcessBuilder pbCheckForImage = new ProcessBuilder(containerTool, "images", "-q", containerImage + ":latest");
        ProcessBuilder pb = new ProcessBuilder(containerTool, "build", "-f", dockerfile.toString(), "-t", containerImage, ".");

        String imageId = getFirstProcessResultLine(pbCheckForImage);
        if(imageId == null) {
            pb.inheritIO();
        } else {
            nativeImage.showMessage(String.format("%sReusing container image %s.", BUNDLE_INFO_MESSAGE_PREFIX, containerImage));
        }

        Process p = null;
        try {
            p = pb.start();
            int status = p.waitFor();
            if(status == 0 && imageId != null) {
                Stream<String> result = (new BufferedReader(new InputStreamReader(p.getInputStream()))).lines();
                if(!imageId.equals(getFirstProcessResultLine(pbCheckForImage))) {
                    nativeImage.showMessage(String.format("%sUpdated container image %s.", BUNDLE_INFO_MESSAGE_PREFIX, containerImage));
                    result.forEach(System.out::println);
                }
            }
            return status;
        } catch (IOException | InterruptedException e) {
            throw NativeImage.showError(e.getMessage());
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    private boolean isToolAvailable(String tool) {
        return Arrays.stream(SubstrateUtil.split(System.getenv("PATH"), ":"))
                .map(str -> Path.of(str).resolve(tool))
                .anyMatch(Files::isExecutable);
    }

    private String getContainerToolVersion(String tool) {
        ProcessBuilder pb = new ProcessBuilder(tool, "--version");
        return getFirstProcessResultLine(pb);
    }

    private boolean isRootlessDocker() {
        ProcessBuilder pb = new ProcessBuilder("docker", "context", "show");
        return getFirstProcessResultLine(pb).equals("rootless");
    }

    private String getFirstProcessResultLine(ProcessBuilder pb) {
        Process p = null;
        try {
            p = pb.start();
            p.waitFor();
            return (new BufferedReader(new InputStreamReader(p.getInputStream()))).readLine();
        } catch (IOException | InterruptedException e) {
            throw NativeImage.showError(e.getMessage());
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    List<String> createContainerCommand(Path argFile, Path builderArgFile) {
        Path containerRoot = Path.of("/");
        return List.of(containerTool, "run", "--network=none", "--rm",
                "--mount", "type=bind,source=" + nativeImage.config.getJavaHome() + ",target=" + containerGraalVMHome + ",readonly",
                "--mount", "type=bind,source=" + inputDir + ",target=" + containerRoot.resolve(rootDir.relativize(inputDir)) + ",readonly",
                "--mount", "type=bind,source=" + outputDir + ",target=" + containerRoot.resolve(rootDir.relativize(outputDir)),
                "--mount", "type=bind,source=" + argFile + ",target=" + argFile + ",readonly",
                "--mount", "type=bind,source=" + builderArgFile + ",target=" + builderArgFile + ",readonly",
                containerImage);
    }

    private BundleSupport(NativeImage nativeImage) {
        Objects.requireNonNull(nativeImage);
        this.nativeImage = nativeImage;

        loadBundle = false;
        writeBundle = true;
        try {
            rootDir = createBundleRootDir();
            bundleProperties = new BundleProperties();

            inputDir = rootDir.resolve("input");
            stageDir = Files.createDirectories(inputDir.resolve("stage"));
            auxiliaryDir = Files.createDirectories(inputDir.resolve("auxiliary"));
            Path classesDir = inputDir.resolve("classes");
            classPathDir = Files.createDirectories(classesDir.resolve("cp"));
            modulePathDir = Files.createDirectories(classesDir.resolve("p"));
            outputDir = rootDir.resolve("output");
            imagePathOutputDir = Files.createDirectories(outputDir.resolve("default"));
            auxiliaryOutputDir = Files.createDirectories(outputDir.resolve("other"));
        } catch (IOException e) {
            throw NativeImage.showError("Unable to create bundle directory layout", e);
        }
        forceBuilderOnClasspath = !nativeImage.config.modulePathBuild;
        nativeImageArgs = nativeImage.getNativeImageArgs();
    }

    private BundleSupport(NativeImage nativeImage, String bundleFilenameArg) {
        Objects.requireNonNull(nativeImage);
        this.nativeImage = nativeImage;

        loadBundle = true;
        writeBundle = false;

        Objects.requireNonNull(bundleFilenameArg);
        updateBundleLocation(Path.of(bundleFilenameArg), false);

        try {
            rootDir = createBundleRootDir();
            bundleProperties = new BundleProperties();

            outputDir = rootDir.resolve("output");
            String originalOutputDirName = outputDir.getFileName().toString() + ORIGINAL_DIR_EXTENSION;

            Path bundleFilePath = bundlePath.resolve(bundleName + BUNDLE_FILE_EXTENSION);
            try (JarFile archive = new JarFile(bundleFilePath.toFile())) {
                Enumeration<JarEntry> jarEntries = archive.entries();
                while (jarEntries.hasMoreElements() && !deleteBundleRoot.get()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    Path bundleEntry = rootDir.resolve(jarEntry.getName());
                    if (bundleEntry.startsWith(outputDir)) {
                        /* Extract original output to different path */
                        bundleEntry = rootDir.resolve(originalOutputDirName).resolve(outputDir.relativize(bundleEntry));
                    }
                    try {
                        Path bundleFileParent = bundleEntry.getParent();
                        if (bundleFileParent != null) {
                            Files.createDirectories(bundleFileParent);
                        }
                        Files.copy(archive.getInputStream(jarEntry), bundleEntry);
                    } catch (IOException e) {
                        throw NativeImage.showError("Unable to copy " + jarEntry.getName() + " from bundle " + bundleEntry + " to " + bundleEntry, e);
                    }
                }
            }
        } catch (IOException e) {
            throw NativeImage.showError("Unable to expand bundle directory layout from bundle file " + bundleName + BUNDLE_FILE_EXTENSION, e);
        }

        if (deleteBundleRoot.get()) {
            /* Abort image build request without error message and exit with 0 */
            throw NativeImage.showError(null, null, 0);
        }

        bundleProperties.loadAndVerify();
        forceBuilderOnClasspath = bundleProperties.forceBuilderOnClasspath();
        nativeImage.config.modulePathBuild = !forceBuilderOnClasspath;

        try {
            inputDir = rootDir.resolve("input");
            stageDir = Files.createDirectories(inputDir.resolve("stage"));
            auxiliaryDir = Files.createDirectories(inputDir.resolve("auxiliary"));
            Path classesDir = inputDir.resolve("classes");
            classPathDir = Files.createDirectories(classesDir.resolve("cp"));
            modulePathDir = Files.createDirectories(classesDir.resolve("p"));
            imagePathOutputDir = Files.createDirectories(outputDir.resolve("default"));
            auxiliaryOutputDir = Files.createDirectories(outputDir.resolve("other"));
        } catch (IOException e) {
            throw NativeImage.showError("Unable to create bundle directory layout", e);
        }

        Path pathCanonicalizationsFile = stageDir.resolve("path_canonicalizations.json");
        try (Reader reader = Files.newBufferedReader(pathCanonicalizationsFile)) {
            new PathMapParser(pathCanonicalizations).parseAndRegister(reader);
        } catch (IOException e) {
            throw NativeImage.showError("Failed to read bundle-file " + pathCanonicalizationsFile, e);
        }
        Path pathSubstitutionsFile = stageDir.resolve("path_substitutions.json");
        try (Reader reader = Files.newBufferedReader(pathSubstitutionsFile)) {
            new PathMapParser(pathSubstitutions).parseAndRegister(reader);
        } catch (IOException e) {
            throw NativeImage.showError("Failed to read bundle-file " + pathSubstitutionsFile, e);
        }
        Path environmentFile = stageDir.resolve("environment.json");
        if (Files.isReadable(environmentFile)) {
            try (Reader reader = Files.newBufferedReader(environmentFile)) {
                new EnvironmentParser(nativeImage.imageBuilderEnvironment).parseAndRegister(reader);
            } catch (IOException e) {
                throw NativeImage.showError("Failed to read bundle-file " + environmentFile, e);
            }
        }

        Path containerFile = stageDir.resolve("container.json");
        if(Files.exists(containerFile)) {
            try (Reader reader = Files.newBufferedReader(containerFile)) {
                EconomicMap<String, Object> json = JSONParser.parseDict(reader);
                if(json.get(containerImageJsonKey) != null) bundleContainerImage =  json.get(containerImageJsonKey).toString();
                if(json.get(containerToolJsonKey) != null) bundleContainerTool = json.get(containerToolJsonKey).toString();
                if(json.get(containerToolVersionJsonKey) != null) bundleContainerToolVersion = json.get(containerToolVersionJsonKey).toString();
            } catch (IOException e) {
                throw NativeImage.showError("Failed to read bundle-file " + pathSubstitutionsFile, e);
            }
            if(bundleContainerTool != null) {
                String containerToolVersionString = bundleContainerToolVersion == null ? "" : String.format(" (%s)", bundleContainerToolVersion);
                nativeImage.showMessage(String.format("%sBundled native-image was created in a container with %s%s.", BUNDLE_INFO_MESSAGE_PREFIX, bundleContainerTool, containerToolVersionString));
                if(useContainer) {
                    nativeImage.showMessage(String.format("%sUsing %s for native-image container build. Specify other container tool with option '%s'.", BUNDLE_INFO_MESSAGE_PREFIX, bundleContainerTool, ExtendedBundleOptions.container));
                }
            }
        }

        bundleDockerfile = stageDir.resolve("Dockerfile");
        if(!Files.isReadable(bundleDockerfile)) {
            bundleDockerfile = null;
        }


        Path buildArgsFile = stageDir.resolve("build.json");
        try (Reader reader = Files.newBufferedReader(buildArgsFile)) {
            List<String> buildArgsFromFile = new ArrayList<>();
            new BuildArgsParser(buildArgsFromFile).parseAndRegister(reader);
            nativeImageArgs = Collections.unmodifiableList(buildArgsFromFile);
        } catch (IOException e) {
            throw NativeImage.showError("Failed to read bundle-file " + buildArgsFile, e);
        }
    }

    private final AtomicBoolean deleteBundleRoot = new AtomicBoolean();

    private Path createBundleRootDir() throws IOException {
        Path bundleRoot = Files.createTempDirectory(BUNDLE_TEMP_DIR_PREFIX);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            deleteBundleRoot.set(true);
            nativeImage.deleteAllFiles(bundleRoot);
        }));
        return bundleRoot;
    }

    public List<String> getNativeImageArgs() {
        return nativeImageArgs;
    }

    Path recordCanonicalization(Path before, Path after) {
        if (before.startsWith(rootDir)) {
            nativeImage.showVerboseMessage(nativeImage.isVVerbose(), "RecordCanonicalization Skip: " + before);
            return before;
        }
        if (after.startsWith(nativeImage.config.getJavaHome())) {
            return after;
        }
        nativeImage.showVerboseMessage(nativeImage.isVVerbose(), "RecordCanonicalization src: " + before + ", dst: " + after);
        pathCanonicalizations.put(before, after);
        return after;
    }

    Path restoreCanonicalization(Path before) {
        Path after = pathCanonicalizations.get(before);
        nativeImage.showVerboseMessage(after != null && nativeImage.isVVerbose(), "RestoreCanonicalization src: " + before + ", dst: " + after);
        return after;
    }

    void replacePathsForContainerBuild(List<String> arguments) {
        arguments.replaceAll(arg -> arg
                .replace(nativeImage.config.getJavaHome().toString(), containerGraalVMHome.toString())
                .replace(rootDir.toString(), "")
        );
    }


    Path substituteAuxiliaryPath(Path origPath, BundleMember.Role bundleMemberRole) {
        Path destinationDir = switch (bundleMemberRole) {
            case Input -> auxiliaryDir;
            case Output -> auxiliaryOutputDir;
            case Ignore -> null;
        };
        if (destinationDir == null) {
            return origPath;
        }
        return substitutePath(origPath, destinationDir);
    }

    Path substituteImagePath(Path origPath) {
        pathSubstitutions.put(origPath, rootDir.relativize(imagePathOutputDir));
        return imagePathOutputDir;
    }

    Path substituteClassPath(Path origPath) {
        try {
            return substitutePath(origPath, classPathDir);
        } catch (BundlePathSubstitutionError error) {
            throw NativeImage.showError("Failed to prepare class-path entry '" + error.origPath + "' for bundle inclusion.", error);
        }
    }

    Path substituteModulePath(Path origPath) {
        try {
            return substitutePath(origPath, modulePathDir);
        } catch (BundlePathSubstitutionError error) {
            throw NativeImage.showError("Failed to prepare module-path entry '" + error.origPath + "' for bundle inclusion.", error);
        }
    }

    @SuppressWarnings("serial")
    static final class BundlePathSubstitutionError extends Error {
        public final Path origPath;

        BundlePathSubstitutionError(String message, Path origPath) {
            super(message);
            this.origPath = origPath;
        }
    }

    @SuppressWarnings("try")
    private Path substitutePath(Path origPath, Path destinationDir) {
        assert destinationDir.startsWith(rootDir);

        if (origPath.startsWith(rootDir)) {
            nativeImage.showVerboseMessage(nativeImage.isVVerbose(), "RecordSubstitution/RestoreSubstitution Skip: " + origPath);
            return origPath;
        }

        Path previousRelativeSubstitutedPath = pathSubstitutions.get(origPath);
        if (previousRelativeSubstitutedPath != null) {
            nativeImage.showVerboseMessage(nativeImage.isVVerbose(), "RestoreSubstitution src: " + origPath + ", dst: " + previousRelativeSubstitutedPath);
            return rootDir.resolve(previousRelativeSubstitutedPath);
        }

        if (origPath.startsWith(nativeImage.config.getJavaHome())) {
            /* If origPath comes from native-image itself, substituting is not needed. */
            return origPath;
        }

        boolean forbiddenPath = false;
        if (!OS.WINDOWS.isCurrent()) {
            Path tmpPath = ClassUtil.CLASS_MODULE_PATH_EXCLUDE_DIRECTORIES_ROOT.resolve("tmp");
            boolean subdirInTmp = origPath.startsWith(tmpPath) && !origPath.equals(tmpPath);
            if (!subdirInTmp) {
                Set<Path> forbiddenPaths = new HashSet<>(ClassUtil.CLASS_MODULE_PATH_EXCLUDE_DIRECTORIES);
                forbiddenPaths.add(rootDir);
                for (Path path : forbiddenPaths) {
                    if (origPath.startsWith(path)) {
                        forbiddenPath = true;
                        break;
                    }
                }
            }
        }
        for (Path rootDirectory : FileSystems.getDefault().getRootDirectories()) {
            /* Refuse /, C:, D:, ... */
            if (origPath.equals(rootDirectory)) {
                forbiddenPath = true;
                break;
            }
        }
        if (forbiddenPath) {
            throw new BundlePathSubstitutionError("Bundles do not allow inclusion of directory " + origPath, origPath);
        }

        boolean isOutputPath = destinationDir.startsWith(outputDir);

        if (!isOutputPath && !Files.isReadable(origPath)) {
            /* Prevent subsequent retries to substitute invalid paths */
            pathSubstitutions.put(origPath, origPath);
            return origPath;
        }

        // TODO: Report error if overlapping dir-trees are passed in

        String origFileName = origPath.getFileName().toString();
        int extensionPos = origFileName.lastIndexOf('.');
        String baseName;
        String extension;
        if (extensionPos > 0) {
            baseName = origFileName.substring(0, extensionPos);
            extension = origFileName.substring(extensionPos);
        } else {
            baseName = origFileName;
            extension = "";
        }

        Path substitutedPath = destinationDir.resolve(baseName + extension);
        int collisionIndex = 0;
        while (Files.exists(substitutedPath)) {
            collisionIndex += 1;
            substitutedPath = destinationDir.resolve(baseName + "_" + collisionIndex + extension);
        }

        if (!isOutputPath) {
            copyFiles(origPath, substitutedPath, false);
        }

        Path relativeSubstitutedPath = rootDir.relativize(substitutedPath);
        nativeImage.showVerboseMessage(nativeImage.isVVerbose(), "RecordSubstitution src: " + origPath + ", dst: " + relativeSubstitutedPath);
        pathSubstitutions.put(origPath, relativeSubstitutedPath);
        return substitutedPath;
    }

    Path originalPath(Path substitutedPath) {
        Path relativeSubstitutedPath = rootDir.relativize(substitutedPath);
        for (Map.Entry<Path, Path> entry : pathSubstitutions.entrySet()) {
            if (entry.getValue().equals(relativeSubstitutedPath)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void copyFiles(Path source, Path target, boolean overwrite) {
        nativeImage.showVerboseMessage(nativeImage.isVVerbose(), "> Copy files from " + source + " to " + target);
        if (Files.isDirectory(source)) {
            try (Stream<Path> walk = Files.walk(source)) {
                walk.forEach(sourcePath -> copyFile(sourcePath, target.resolve(source.relativize(sourcePath)), overwrite));
            } catch (IOException e) {
                throw NativeImage.showError("Failed to iterate through directory " + source, e);
            }
        } else {
            copyFile(source, target, overwrite);
        }
    }

    private void copyFile(Path sourceFile, Path target, boolean overwrite) {
        try {
            nativeImage.showVerboseMessage(nativeImage.isVVVerbose(), "> Copy " + sourceFile + " to " + target);
            if (overwrite && Files.isDirectory(sourceFile) && Files.isDirectory(target)) {
                return;
            }
            CopyOption[] options = overwrite ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0];
            Files.copy(sourceFile, target, options);
        } catch (IOException e) {
            throw NativeImage.showError("Failed to copy " + sourceFile + " to " + target, e);
        }
    }

    void complete() {
        boolean writeOutput;
        try (Stream<Path> pathOutputFiles = Files.list(imagePathOutputDir); Stream<Path> auxiliaryOutputFiles = Files.list(auxiliaryOutputDir)) {
            writeOutput = (pathOutputFiles.findAny().isPresent() || auxiliaryOutputFiles.findAny().isPresent()) && !deleteBundleRoot.get();
        } catch (IOException e) {
            writeOutput = false;
        }

        /*
         * In the unlikely case of writing a bundle but no location got specified so far, provide a
         * final fallback here. Can happen when something goes wrong in bundle processing itself.
         */
        if (bundlePath == null) {
            bundlePath = nativeImage.config.getWorkingDirectory();
            bundleName = "unknown";
        }

        if (!nativeImage.isDryRun() && (writeOutput || writeBundle)) {
            nativeImage.showNewline();
        }

        if (writeOutput) {
            Path externalOutputDir = bundlePath.resolve(bundleName + "." + outputDir.getFileName());
            copyFiles(outputDir, externalOutputDir, true);
            nativeImage.showMessage(BUNDLE_INFO_MESSAGE_PREFIX + "Bundle build output written to " + externalOutputDir);
        }

        try {
            if (writeBundle) {
                Path bundleFilePath = writeBundle();
                nativeImage.showMessage(BUNDLE_INFO_MESSAGE_PREFIX + "Bundle written to " + bundleFilePath);
            }
        } finally {
            nativeImage.showNewline();
        }
    }

    void updateBundleLocation(Path bundleFile, boolean redefine) {
        if (redefine) {
            bundlePath = null;
            bundleName = null;
        }

        if (bundlePath != null) {
            Objects.requireNonNull(bundleName);
            /* Bundle location is already set */
            return;
        }
        Path bundleFilePath = bundleFile.toAbsolutePath();
        String bundleFileName = bundleFile.getFileName().toString();
        if (!bundleFileName.endsWith(BUNDLE_FILE_EXTENSION)) {
            throw NativeImage.showError("The given bundle file " + bundleFileName + " does not end with '" + BUNDLE_FILE_EXTENSION + "'");
        }
        if (Files.isDirectory(bundleFilePath)) {
            throw NativeImage.showError("The given bundle file " + bundleFileName + " is a directory and not a file");
        }
        if (loadBundle && !redefine) {
            if (!Files.isReadable(bundleFilePath)) {
                throw NativeImage.showError("The given bundle file " + bundleFileName + " cannot be read.");
            }
        }
        Path newBundlePath = bundleFilePath.getParent();
        if (writeBundle) {
            if (!Files.isWritable(newBundlePath)) {
                throw NativeImage.showError("The bundle file directory " + newBundlePath + " is not writeable.");
            }
            if (Files.exists(bundleFilePath) && !Files.isWritable(bundleFilePath)) {
                throw NativeImage.showError("The given bundle file " + bundleFileName + " is not writeable.");
            }
        }
        bundlePath = newBundlePath;
        bundleName = bundleFileName.substring(0, bundleFileName.length() - BUNDLE_FILE_EXTENSION.length());
    }

    private Path writeBundle() {
        String originalOutputDirName = outputDir.getFileName().toString() + ORIGINAL_DIR_EXTENSION;
        Path originalOutputDir = rootDir.resolve(originalOutputDirName);
        if (Files.exists(originalOutputDir)) {
            nativeImage.deleteAllFiles(originalOutputDir);
        }

        Path metaInfDir = rootDir.resolve(JarFile.MANIFEST_NAME);
        if (Files.exists(metaInfDir)) {
            nativeImage.deleteAllFiles(metaInfDir);
        }

        Path pathCanonicalizationsFile = stageDir.resolve("path_canonicalizations.json");
        try (JsonWriter writer = new JsonWriter(pathCanonicalizationsFile)) {
            /* Printing as list with defined sort-order ensures useful diffs are possible */
            JsonPrinter.printCollection(writer, pathCanonicalizations.entrySet(), Map.Entry.comparingByKey(), BundleSupport::printPathMapping);
        } catch (IOException e) {
            throw NativeImage.showError("Failed to write bundle-file " + pathCanonicalizationsFile, e);
        }
        Path pathSubstitutionsFile = stageDir.resolve("path_substitutions.json");
        try (JsonWriter writer = new JsonWriter(pathSubstitutionsFile)) {
            /* Printing as list with defined sort-order ensures useful diffs are possible */
            JsonPrinter.printCollection(writer, pathSubstitutions.entrySet(), Map.Entry.comparingByKey(), BundleSupport::printPathMapping);
        } catch (IOException e) {
            throw NativeImage.showError("Failed to write bundle-file " + pathSubstitutionsFile, e);
        }
        Path environmentFile = stageDir.resolve("environment.json");
        try (JsonWriter writer = new JsonWriter(environmentFile)) {
            /* Printing as list with defined sort-order ensures useful diffs are possible */
            JsonPrinter.printCollection(writer, nativeImage.imageBuilderEnvironment.entrySet(), Map.Entry.comparingByKey(), BundleSupport::printEnvironmentVariable);
        } catch (IOException e) {
            throw NativeImage.showError("Failed to write bundle-file " + environmentFile, e);
        }

        if(useContainer) {
            Map<String, Object> containerInfo = new HashMap<>();
            if(containerImage != null) containerInfo.put(containerImageJsonKey, containerImage);
            if(containerTool != null) containerInfo.put(containerToolJsonKey, containerTool);
            if(containerToolVersion != null) containerInfo.put(containerToolVersionJsonKey, containerToolVersion);

            if(!containerInfo.isEmpty()) {
                Path containerFile = stageDir.resolve("container.json");
                try (JsonWriter writer = new JsonWriter(containerFile)) {
                    writer.print(containerInfo);
                } catch (IOException e) {
                    throw NativeImage.showError("Failed to write bundle-file " + containerFile, e);
                }
            }

            if(dockerfile != null) {
                Path bundleDockerfile = stageDir.resolve("Dockerfile");
                try {
                    Files.copy(dockerfile, bundleDockerfile);
                } catch (IOException e) {
                    throw NativeImage.showError("Failed to write bundle-file " + bundleDockerfile, e);
                }
            }
        }

        Path buildArgsFile = stageDir.resolve("build.json");
        try (JsonWriter writer = new JsonWriter(buildArgsFile)) {
            List<String> equalsNonBundleOptions = List.of(CmdLineOptionHandler.VERBOSE_OPTION, CmdLineOptionHandler.DRY_RUN_OPTION);
            List<String> startsWithNonBundleOptions = List.of(BUNDLE_OPTION, DefaultOptionHandler.ADD_ENV_VAR_OPTION, nativeImage.oHPath);
            ArrayList<String> bundleArgs = new ArrayList<>(updatedNativeImageArgs != null ? updatedNativeImageArgs : nativeImageArgs);
            ListIterator<String> bundleArgsIterator = bundleArgs.listIterator();
            while (bundleArgsIterator.hasNext()) {
                String arg = bundleArgsIterator.next();
                if (equalsNonBundleOptions.contains(arg) || startsWithNonBundleOptions.stream().anyMatch(arg::startsWith)) {
                    bundleArgsIterator.remove();
                } else if (arg.startsWith("-Dllvm.bin.dir=")) {
                    Optional<String> existing = nativeImage.config.getBuildArgs().stream().filter(a -> a.startsWith("-Dllvm.bin.dir=")).findFirst();
                    if (existing.isPresent() && !existing.get().equals(arg)) {
                        throw NativeImage.showError("Bundle native-image argument '" + arg + "' conflicts with existing '" + existing.get() + "'.");
                    }
                    bundleArgsIterator.remove();
                }
            }
            /* Printing as list with defined sort-order ensures useful diffs are possible */
            JsonPrinter.printCollection(writer, bundleArgs, null, BundleSupport::printBuildArg);
        } catch (IOException e) {
            throw NativeImage.showError("Failed to write bundle-file " + buildArgsFile, e);
        }

        bundleProperties.write();

        Path bundleFilePath = bundlePath.resolve(bundleName + BUNDLE_FILE_EXTENSION);
        try (JarOutputStream jarOutStream = new JarOutputStream(Files.newOutputStream(bundleFilePath), createManifest())) {
            try (Stream<Path> walk = Files.walk(rootDir)) {
                walk.filter(Predicate.not(Files::isDirectory)).forEach(bundleEntry -> {
                    String jarEntryName = rootDir.relativize(bundleEntry).toString();
                    JarEntry entry = new JarEntry(jarEntryName.replace(File.separator, "/"));
                    try {
                        entry.setTime(Files.getLastModifiedTime(bundleEntry).toMillis());
                        jarOutStream.putNextEntry(entry);
                        Files.copy(bundleEntry, jarOutStream);
                        jarOutStream.closeEntry();
                    } catch (IOException e) {
                        throw NativeImage.showError("Failed to copy " + bundleEntry + " into bundle file " + bundleFilePath.getFileName(), e);
                    }
                });
            }
        } catch (IOException e) {
            throw NativeImage.showError("Failed to create bundle file " + bundleFilePath.getFileName(), e);
        }

        return bundleFilePath;
    }

    private static Manifest createManifest() {
        Manifest mf = new Manifest();
        Attributes attributes = mf.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        /* If we add run-bundle-as-java-application a launcher mainclass would be added here */
        return mf;
    }

    private static final String substitutionMapSrcField = "src";
    private static final String substitutionMapDstField = "dst";

    private static void printPathMapping(Map.Entry<Path, Path> entry, JsonWriter w) throws IOException {
        w.append('{').quote(substitutionMapSrcField).append(':').quote(entry.getKey());
        w.append(',').quote(substitutionMapDstField).append(':').quote(entry.getValue());
        w.append('}');
    }

    private static void printBuildArg(String entry, JsonWriter w) throws IOException {
        w.quote(entry);
    }

    private static final String environmentKeyField = "key";
    private static final String environmentValueField = "val";

    private static void printEnvironmentVariable(Map.Entry<String, String> entry, JsonWriter w) throws IOException {
        if (entry.getValue() == null) {
            throw NativeImage.showError("Storing environment variable '" + entry.getKey() + "' in bundle requires to have its value defined.");
        }
        w.append('{').quote(environmentKeyField).append(':').quote(entry.getKey());
        w.append(',').quote(environmentValueField).append(':').quote(entry.getValue());
        w.append('}');
    }

    private static final class PathMapParser extends ConfigurationParser {

        private final Map<Path, Path> pathMap;

        private PathMapParser(Map<Path, Path> pathMap) {
            super(true);
            this.pathMap = pathMap;
        }

        @Override
        public void parseAndRegister(Object json, URI origin) {
            for (var rawEntry : asList(json, "Expected a list of path substitution objects")) {
                var entry = asMap(rawEntry, "Expected a substitution object");
                Object srcPathString = entry.get(substitutionMapSrcField);
                if (srcPathString == null) {
                    throw new JSONParserException("Expected " + substitutionMapSrcField + "-field in substitution object");
                }
                Object dstPathString = entry.get(substitutionMapDstField);
                if (dstPathString == null) {
                    throw new JSONParserException("Expected " + substitutionMapDstField + "-field in substitution object");
                }
                pathMap.put(Path.of(srcPathString.toString()), Path.of(dstPathString.toString()));
            }
        }
    }

    private static final class EnvironmentParser extends ConfigurationParser {

        private final Map<String, String> environment;

        private EnvironmentParser(Map<String, String> environment) {
            super(true);
            environment.clear();
            this.environment = environment;
        }

        @Override
        public void parseAndRegister(Object json, URI origin) {
            for (var rawEntry : asList(json, "Expected a list of environment variable objects")) {
                var entry = asMap(rawEntry, "Expected a environment variable object");
                Object envVarKeyString = entry.get(environmentKeyField);
                if (envVarKeyString == null) {
                    throw new JSONParserException("Expected " + environmentKeyField + "-field in environment variable object");
                }
                Object envVarValueString = entry.get(environmentValueField);
                if (envVarValueString == null) {
                    throw new JSONParserException("Expected " + environmentValueField + "-field in environment variable object");
                }
                environment.put(envVarKeyString.toString(), envVarValueString.toString());
            }
        }
    }

    private static final class BuildArgsParser extends ConfigurationParser {

        private final List<String> args;

        private BuildArgsParser(List<String> args) {
            super(true);
            this.args = args;
        }

        @Override
        public void parseAndRegister(Object json, URI origin) {
            for (var arg : asList(json, "Expected a list of arguments")) {
                args.add(arg.toString());
            }
        }
    }

    private static final Path bundlePropertiesFileName = Path.of("META-INF/nibundle.properties");

    private final class BundleProperties {

        private static final String PROPERTY_KEY_BUNDLE_FILE_VERSION_MAJOR = "BundleFileVersionMajor";
        private static final String PROPERTY_KEY_BUNDLE_FILE_VERSION_MINOR = "BundleFileVersionMinor";
        private static final String PROPERTY_KEY_BUNDLE_FILE_CREATION_TIMESTAMP = "BundleFileCreationTimestamp";
        private static final String PROPERTY_KEY_BUILDER_ON_CLASSPATH = "BuilderOnClasspath";
        private static final String PROPERTY_KEY_IMAGE_BUILT = "ImageBuilt";
        private static final String PROPERTY_KEY_BUILT_WITH_CONTAINER = "BuiltWithContainer";
        private static final String PROPERTY_KEY_NATIVE_IMAGE_PLATFORM = "NativeImagePlatform";
        private static final String PROPERTY_KEY_NATIVE_IMAGE_VENDOR = "NativeImageVendor";
        private static final String PROPERTY_KEY_NATIVE_IMAGE_VERSION = "NativeImageVersion";

        private final Path bundlePropertiesFile;
        private final Map<String, String> properties;

        private BundleProperties() {
            Objects.requireNonNull(rootDir);
            Objects.requireNonNull(nativeImage);

            bundlePropertiesFile = rootDir.resolve(bundlePropertiesFileName);
            properties = new HashMap<>();
        }

        private void loadAndVerify() {
            Objects.requireNonNull(bundleName);

            String bundleFileName = bundlePath.resolve(bundleName + BUNDLE_FILE_EXTENSION).toString();
            if (!Files.isReadable(bundlePropertiesFile)) {
                throw NativeImage.showError("The given bundle file " + bundleFileName + " does not contain a bundle properties file");
            }

            properties.putAll(NativeImage.loadProperties(bundlePropertiesFile));
            String fileVersionKey = PROPERTY_KEY_BUNDLE_FILE_VERSION_MAJOR;
            try {
                int major = Integer.parseInt(properties.getOrDefault(fileVersionKey, "-1"));
                fileVersionKey = PROPERTY_KEY_BUNDLE_FILE_VERSION_MINOR;
                int minor = Integer.parseInt(properties.getOrDefault(fileVersionKey, "-1"));
                String message = String.format("The given bundle file %s was created with newer bundle-file-format version %d.%d" +
                                " (current %d.%d). Update to the latest version of native-image.", bundleFileName, major, minor, BUNDLE_FILE_FORMAT_VERSION_MAJOR, BUNDLE_FILE_FORMAT_VERSION_MINOR);
                if (major > BUNDLE_FILE_FORMAT_VERSION_MAJOR) {
                    throw NativeImage.showError(message);
                } else if (major == BUNDLE_FILE_FORMAT_VERSION_MAJOR) {
                    if (minor > BUNDLE_FILE_FORMAT_VERSION_MINOR) {
                        NativeImage.showWarning(message);
                    }
                }
            } catch (NumberFormatException e) {
                throw NativeImage.showError(fileVersionKey + " in " + bundlePropertiesFileName + " is missing or ill-defined", e);
            }
            String bundleVendor = properties.getOrDefault(PROPERTY_KEY_NATIVE_IMAGE_VENDOR, "unknown");
            String javaVmVendor = System.getProperty("java.vm.vendor");
            String currentVendor = bundleVendor.equals(javaVmVendor) ? "" : " != '" + javaVmVendor + "'";
            String bundleVersion = properties.getOrDefault(PROPERTY_KEY_NATIVE_IMAGE_VERSION, "unknown");
            String javaVmVersion = System.getProperty("java.vm.version");
            String currentVersion = bundleVersion.equals(javaVmVersion) ? "" : " != '" + javaVmVersion + "'";
            String bundlePlatform = properties.getOrDefault(PROPERTY_KEY_NATIVE_IMAGE_PLATFORM, "unknown");
            String currentPlatform = bundlePlatform.equals(NativeImage.platform) ? "" : " != '" + NativeImage.platform + "'";
            String bundleCreationTimestamp = properties.getOrDefault(PROPERTY_KEY_BUNDLE_FILE_CREATION_TIMESTAMP, "");
            String localDateStr;
            try {
                ZonedDateTime dateTime = ZonedDateTime.parse(bundleCreationTimestamp, DateTimeFormatter.ISO_DATE_TIME);
                localDateStr = dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL));
            } catch (DateTimeParseException e) {
                localDateStr = "unknown time";
            }
            nativeImage.showNewline();
            nativeImage.showMessage("%sLoaded Bundle from %s", BUNDLE_INFO_MESSAGE_PREFIX, bundleFileName);
            nativeImage.showMessage("%sBundle created at '%s'", BUNDLE_INFO_MESSAGE_PREFIX, localDateStr);
            nativeImage.showMessage("%sUsing version: '%s'%s (vendor '%s'%s) on platform: '%s'%s", BUNDLE_INFO_MESSAGE_PREFIX,
                            bundleVersion, currentVersion,
                            bundleVendor, currentVendor,
                            bundlePlatform, currentPlatform);
        }

        private boolean forceBuilderOnClasspath() {
            assert !properties.isEmpty() : "Needs to be called after loadAndVerify()";
            return Boolean.parseBoolean(properties.getOrDefault(PROPERTY_KEY_BUILDER_ON_CLASSPATH, Boolean.FALSE.toString()));
        }

        private void write() {
            properties.put(PROPERTY_KEY_BUNDLE_FILE_VERSION_MAJOR, String.valueOf(BUNDLE_FILE_FORMAT_VERSION_MAJOR));
            properties.put(PROPERTY_KEY_BUNDLE_FILE_VERSION_MINOR, String.valueOf(BUNDLE_FILE_FORMAT_VERSION_MINOR));
            properties.put(PROPERTY_KEY_BUNDLE_FILE_CREATION_TIMESTAMP, ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            properties.put(PROPERTY_KEY_BUILDER_ON_CLASSPATH, String.valueOf(forceBuilderOnClasspath));
            boolean imageBuilt = !nativeImage.isDryRun();
            properties.put(PROPERTY_KEY_IMAGE_BUILT, String.valueOf(imageBuilt));
            if (imageBuilt) {
                properties.put(PROPERTY_KEY_BUILT_WITH_CONTAINER, String.valueOf(useContainer));
            }
            properties.put(PROPERTY_KEY_NATIVE_IMAGE_PLATFORM, NativeImage.platform);
            properties.put(PROPERTY_KEY_NATIVE_IMAGE_VENDOR, System.getProperty("java.vm.vendor"));
            properties.put(PROPERTY_KEY_NATIVE_IMAGE_VERSION, System.getProperty("java.vm.version"));
            NativeImage.ensureDirectoryExists(bundlePropertiesFile.getParent());
            try (OutputStream outputStream = Files.newOutputStream(bundlePropertiesFile)) {
                Properties p = new Properties();
                p.putAll(properties);
                p.store(outputStream, "Native Image bundle file properties");
            } catch (IOException e) {
                throw NativeImage.showError("Creating bundle properties file " + bundlePropertiesFileName + " failed", e);
            }
        }
    }
}
