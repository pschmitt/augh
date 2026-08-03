{
  description = "aughhhh Android development environment";

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
    in {
      devShells.${system}.default = pkgs.mkShell {
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
    };
}
