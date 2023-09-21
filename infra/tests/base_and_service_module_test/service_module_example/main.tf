terraform {
  required_version = ">= 1.5.7"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.16.1"
    }
  }

  backend "s3" {
    key            = "service_module_example"
    bucket         = "learn-spring-160071257600"
    dynamodb_table = "learn-spring-terraform-lock"
    region         = "ap-southeast-1"
  }
}

provider "aws" {
  region = "ap-southeast-1"
}

data "terraform_remote_state" "base_environment" {
  backend = "s3"
  config  = {
    bucket = "learn-spring-160071257600"
    key    = "base_environment"
    region = "ap-southeast-1"
  }
  workspace = terraform.workspace
}
