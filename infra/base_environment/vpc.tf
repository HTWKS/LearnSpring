module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  version = "5.1.2"

  name = local.resource_name_prefix
  cidr = "10.0.0.0/16"

  azs             = [var.singapore-aws-provider-az-a, var.singapore-aws-provider-az-b]
  private_subnets = ["10.0.1.0/24",]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24"]

  enable_vpn_gateway = false
}
