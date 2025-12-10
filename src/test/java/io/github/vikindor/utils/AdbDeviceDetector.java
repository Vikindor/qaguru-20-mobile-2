package io.github.vikindor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Utility to detect connected Android devices via adb.
 *
 * Behavior:
 * - attempts to locate the adb executable (PATH, ANDROID_HOME, ANDROID_SDK_ROOT, common SDK paths)
 * - ignores devices whose id starts with "emulator"
 * - if multiple physical devices are present, returns the first one
 *
 * This class is standalone and does not depend on ConfigProvider.
 */
public final class AdbDeviceDetector {

    private AdbDeviceDetector() {}

    private static final Duration ADB_TIMEOUT = Duration.ofSeconds(5);
    private static volatile String RESOLVED_ADB = null;

    public static DeviceInfo detectDeviceInfo() {
        String deviceId = firstPhysicalDeviceId();
        String platformVersion = platformVersionFor(deviceId);
        return new DeviceInfo(deviceId, platformVersion);
    }

    public static String firstPhysicalDeviceId() {
        CommandResult res = runCommand(resolveAdb(), "devices");

        if (res.timedOut) {
            throw new RuntimeException("adb command timed out. Is adb available and responsive?");
        }
        if (res.exitCode != 0) {
            throw new RuntimeException("adb returned non-zero exit code: " + res.exitCode
                    + ". stderr: " + safe(res.stderr));
        }

        List<String> lines = Arrays.stream(res.stdout.split("\\R"))
                                   .map(String::trim)
                                   .filter(line -> !line.isEmpty())
                                   .collect(Collectors.toList());

        if (lines.isEmpty()) {
            throw new RuntimeException("No output from 'adb devices'. ADB was invoked but produced no output.");
        }

        List<String> deviceLines = lines.stream()
                                        .filter(line -> !line.toLowerCase().startsWith("list of devices"))
                                        .collect(Collectors.toList());

        Optional<String> firstPhysical = deviceLines.stream()
                                                    .filter(line -> line.endsWith("\tdevice") || line.matches("^\\S+\\s+device$"))
                                                    .map(line -> {
                                                        String cleaned = line.replace("\t", " ").trim();
                                                        return cleaned.split("\\s+")[0];
                                                    })
                                                    .filter(id -> !id.startsWith("emulator"))
                                                    .findFirst();

        if (firstPhysical.isPresent()) {
            return firstPhysical.get();
        }

        Optional<String> fallback = deviceLines.stream()
                                               .map(line -> {
                                                   String cleaned = line.replace("\t", " ").trim();
                                                   String[] parts = cleaned.split("\\s+");
                                                   return parts.length > 0 ? parts[0] : "";
                                               })
                                               .filter(id -> !id.isEmpty() && !id.startsWith("emulator"))
                                               .findFirst();

        if (fallback.isPresent()) {
            return fallback.get();
        }

        boolean hasEmu = deviceLines.stream()
                                    .map(line -> line.replace("\t", " ").trim().split("\\s+")[0])
                                    .anyMatch(id -> id.startsWith("emulator"));

        if (hasEmu) {
            throw new NoPhysicalDeviceException("No physical adb device found, only emulators are connected. " +
                    "If you want to use an emulator, run the tests with the appropriate parameter.");
        }

        throw new RuntimeException("No physical adb device found and no usable devices in 'adb devices' output.");
    }

    public static String platformVersionFor(String deviceId) {
        CommandResult r1 = runCommand(resolveAdb(), "-s", deviceId, "shell", "getprop", "ro.build.version.release");
        if (r1.timedOut) {
            throw new RuntimeException("adb shell getprop timed out for device " + deviceId);
        }
        if (r1.exitCode == 0) {
            String ver = r1.stdout.replace("\r", "").trim();
            if (!ver.isEmpty()) return ver;
        }

        CommandResult r2 = runCommand(resolveAdb(), "-s", deviceId, "shell", "getprop", "ro.build.version.sdk");
        if (r2.timedOut) {
            throw new RuntimeException("adb shell getprop timed out for device " + deviceId);
        }
        if (r2.exitCode == 0) {
            String sdk = r2.stdout.replace("\r", "").trim();
            if (!sdk.isEmpty()) return sdk;
        }

        throw new RuntimeException("Could not determine platform version for device " + deviceId
                + ". stdout1: " + safe(r1.stdout) + " stderr1: " + safe(r1.stderr)
                + " stdout2: " + safe(r2.stdout) + " stderr2: " + safe(r2.stderr));
    }

    private static String resolveAdb() {
        if (RESOLVED_ADB != null) return RESOLVED_ADB;

        try {
            CommandResult cr = runCommandInternal(new String[]{"adb", "version"});
            if (!cr.timedOut && cr.exitCode == 0) {
                RESOLVED_ADB = "adb";
                return RESOLVED_ADB;
            }
        } catch (RuntimeException ignored) {}

        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null && !androidHome.trim().isEmpty()) {
            String adbPath = join(androidHome, "platform-tools", executableName());
            if (new File(adbPath).exists()) {
                RESOLVED_ADB = adbPath;
                return RESOLVED_ADB;
            }
        }

        String sdkRoot = System.getenv("ANDROID_SDK_ROOT");
        if (sdkRoot != null && !sdkRoot.trim().isEmpty()) {
            String adbPath = join(sdkRoot, "platform-tools", executableName());
            if (new File(adbPath).exists()) {
                RESOLVED_ADB = adbPath;
                return RESOLVED_ADB;
            }
        }

        String userHome = System.getProperty("user.home");
        String[] common = new String[]{
                join(userHome, "Android", "Sdk", "platform-tools", executableName()),
                join(userHome, "AppData", "Local", "Android", "Sdk", "platform-tools", executableName()), // windows common
                "/usr/local/android-sdk/platform-tools/" + executableName(),
                "/opt/android-sdk/platform-tools/" + executableName(),
                "/usr/bin/" + executableName(),
                "C:\\Android\\platform-tools\\" + executableName()
        };

        for (String path : common) {
            if (path != null && new File(path).exists()) {
                RESOLVED_ADB = path;
                return RESOLVED_ADB;
            }
        }

        throw new RuntimeException("ADB not found. Please add it to PATH or set ANDROID_HOME / ANDROID_SDK_ROOT.");
    }

    private static String join(String... parts) {
        if (parts == null || parts.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p == null) continue;
            if (sb.length() > 0 && !sb.toString().endsWith(File.separator)) sb.append(File.separator);
            sb.append(p.replace("/", File.separator).replace("\\", File.separator));
        }
        return sb.toString();
    }

    private static String executableName() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win") ? "adb.exe" : "adb";
    }

    private static CommandResult runCommand(String... command) {
        return runCommandInternal(command);
    }

    private static CommandResult runCommandInternal(String[] command) {
        Process p = null;
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            p = pb.start();

            try (BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
                 BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {

                long start = System.currentTimeMillis();
                while (p.isAlive()) {
                    while (out.ready()) {
                        String line = out.readLine();
                        if (line == null) break;
                        stdout.append(line).append("\n");
                    }
                    while (err.ready()) {
                        String line = err.readLine();
                        if (line == null) break;
                        stderr.append(line).append("\n");
                    }
                    if (System.currentTimeMillis() - start > ADB_TIMEOUT.toMillis()) {
                        p.destroyForcibly();
                        return new CommandResult(stdout.toString(), stderr.toString(), -1, true);
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }

                String o;
                while ((o = out.readLine()) != null) stdout.append(o).append("\n");
                String e;
                while ((e = err.readLine()) != null) stderr.append(e).append("\n");

                int exit = p.waitFor(1, TimeUnit.SECONDS) ? p.exitValue() : p.exitValue();
                return new CommandResult(stdout.toString(), stderr.toString(), exit, false);
            }
        } catch (Exception ex) {
            String errMsg = "Failed to run command: " + Arrays.toString(command) + ". Exception: " + ex.getMessage();
            throw new RuntimeException(errMsg, ex);
        } finally {
            if (p != null && p.isAlive()) {
                p.destroyForcibly();
            }
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static final class CommandResult {
        final String stdout;
        final String stderr;
        final int exitCode;
        final boolean timedOut;
        CommandResult(String stdout, String stderr, int exitCode, boolean timedOut) {
            this.stdout = stdout == null ? "" : stdout;
            this.stderr = stderr == null ? "" : stderr;
            this.exitCode = exitCode;
            this.timedOut = timedOut;
        }
    }

    public static final class DeviceInfo {
        public final String deviceId;
        public final String platformVersion;

        public DeviceInfo(String deviceId, String platformVersion) {
            this.deviceId = deviceId;
            this.platformVersion = platformVersion;
        }

        @Override
        public String toString() {
            return "DeviceInfo{deviceId='" + deviceId + "', platformVersion='" + platformVersion + "'}";
        }
    }

    public static class NoPhysicalDeviceException extends RuntimeException {
        public NoPhysicalDeviceException(String message) {
            super(message);
        }
    }
}
