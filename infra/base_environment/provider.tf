provider "aws" {
  alias  = "us-east-1"
  region = var.virginia-aws-provider
  default_tags {
    tags = local.default_tags
  }
}

provider "aws" {
  alias  = "ap-southeast-1"
  region = var.singapore-aws-provider
  default_tags {
    tags = local.default_tags
  }
}
provider "aws" {
  region = var.singapore-aws-provider
  default_tags {
    tags = local.default_tags
  }
}