variable "singapore-aws-provider" {
  type = string
}

variable "singapore-aws-provider-az-a" {
  type = string
}

variable "singapore-aws-provider-az-b" {
  type = string
}

variable "virginia-aws-provider" {
  type = string
}

variable "domain_name" {
  type = string
}

variable "subdomain" {
  type = string
}

locals {
  environment_name     = terraform.workspace
  resource_name_prefix = "learn-spring-${local.environment_name}"
  host = "${coalesce(var.subdomain, local.environment_name)}.${var.domain_name}"
  default_tags = {
    Environment = local.environment_name
  }
}