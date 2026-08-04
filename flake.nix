{
  description = "AUGH! Android development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    android-nixpkgs = {
      url = "github:tadfisher/android-nixpkgs";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { self, nixpkgs, android-nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs {
        inherit system;
        config.allowUnfree = true;
      };
      android-sdk = android-nixpkgs.sdk.${system} (sdkPkgs: with sdkPkgs; [
        cmdline-tools-latest
        build-tools-36-1-0
        platform-tools
        platforms-android-36
      ]);

      # Local-only Android SDK selection for capturing tablet screenshots on an emulator instead
      # of a physical device (real devices over network adb are brittle - reconnects, port
      # changes, needing the device physically present). Adds the emulator and an API 34
      # google_apis x86_64 system image on top of the same base tools; kept out of
      # `android-sdk` above so the ordinary Gradle/lint dev shell doesn't pull in the emulator.
      android-sdk-screenshots = android-nixpkgs.sdk.${system} (sdkPkgs: with sdkPkgs; [
        cmdline-tools-latest
        platform-tools
        emulator
        system-images-android-34-google-apis-x86-64
      ]);
    in {
      devShells.${system} = {
        default = pkgs.mkShell {
          packages = with pkgs; [
            jdk21
            just
            ktfmt
            nixfmt
            android-sdk
          ];

          shellHook = ''
            export JAVA_HOME=${pkgs.jdk21}/lib/openjdk
            export ANDROID_SDK_ROOT=${android-sdk}/share/android-sdk
            export ANDROID_HOME=$ANDROID_SDK_ROOT
            export PATH=$PATH:$ANDROID_SDK_ROOT/platform-tools
            export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin
            export PATH=$PATH:$ANDROID_SDK_ROOT/build-tools/36.1.0
            export GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.project.android.aapt2FromMavenOverride=$ANDROID_SDK_ROOT/build-tools/36.1.0/aapt2"
          '';
        };

        # Local-only shell for capturing Play Store screenshots (fastlane/screengrab). Deliberately
        # separate from the default shell: it only needs adb (+ the emulator for tablet
        # screenshots), not the full SDK/Gradle toolchain that AGENTS.md keeps on the remote build
        # hosts.
        screenshots = pkgs.mkShell {
          packages = with pkgs; [ fastlane android-sdk-screenshots ];

          shellHook = ''
            export ANDROID_SDK_ROOT=${android-sdk-screenshots}/share/android-sdk
            export ANDROID_HOME=$ANDROID_SDK_ROOT
            export PATH=$PATH:$ANDROID_SDK_ROOT/platform-tools
            export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin
            export PATH=$PATH:$ANDROID_SDK_ROOT/emulator

            # avdmanager (XDG-aware) and emulator (not) disagree on the default AVD home when
            # unset - avdmanager creates under XDG_CONFIG_HOME, emulator looks in ~/.android. Pin
            # both to the XDG location explicitly so they agree.
            export ANDROID_AVD_HOME="''${XDG_CONFIG_HOME:-$HOME/.config}/.android/avd"
            mkdir -p "$ANDROID_AVD_HOME"
          '';
        };
      };
    };
}
