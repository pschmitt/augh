{
  description = "AUGH! Android development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    android-nixpkgs = {
      url = "github:tadfisher/android-nixpkgs";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    git-hooks = {
      url = "github:cachix/git-hooks.nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    android-app-ci = {
      url = "github:pschmitt/android-app-ci";
      flake = false;
    };
  };

  outputs =
    {
      self,
      nixpkgs,
      android-nixpkgs,
      git-hooks,
      android-app-ci,
    }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs {
        inherit system;
        config.allowUnfree = true;
      };

      androidEnv = import "${android-app-ci}/nix/devshells.nix" {
        inherit pkgs android-nixpkgs system;
        appName = "AUGH!";
        buildToolsVersion = "37.0.0";
        platformVersion = "37-0";
        gitHooksLib = git-hooks.lib;
        screenshotsSystemImage = "system-images-android-34-google-apis-x86-64";
      };
    in
    {
      devShells.${system} = androidEnv.devShells;
      checks.${system} = androidEnv.checks;
    };
}
