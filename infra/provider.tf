provider "aws" {
  alias  = "us-east-1"
  region = var.virginia-aws-provider
}

provider "aws" {
  alias  = "ap-southeast-1"
  region = var.singapore-aws-provider
}