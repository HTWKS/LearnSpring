resource "aws_security_group" "alb" {
  name   = local.resource_name_prefix
  vpc_id = module.vpc.vpc_id

  ingress {
    protocol    = "tcp"
    from_port   = 80
    to_port     = 80
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    protocol    = "tcp"
    from_port   = 443
    to_port     = 443
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress { // TODO
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = module.vpc.private_subnets_cidr_blocks
  }
}

resource "aws_lb" "main" {
  name               = local.resource_name_prefix
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = module.vpc.public_subnets
}

resource "aws_alb_listener" "https" {
  load_balancer_arn = aws_lb.main.id
  port              = 443
  protocol          = "HTTPS"
  certificate_arn   = aws_acm_certificate.cert.arn

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "OK"
      status_code  = "200"
    }
  }

  // Sometimes the cert might not be issued by the time we get here
  // which it should be. I'm not sure depends_on fixes this ...
  // I tried setting a tag with the `id` attribute of the validation
  // which would have covered this, but there's bug in the aws provider
  //
  // https://github.com/hashicorp/terraform-provider-aws/issues/19583
  depends_on = [
    aws_acm_certificate_validation.validation
  ]
}


resource "aws_route53_record" "www" {
  zone_id = data.aws_route53_zone.domain.zone_id
  name    = local.host
  type    = "A"

  alias {
    name                   = aws_lb.main.dns_name
    zone_id                = aws_lb.main.zone_id
    evaluate_target_health = true
  }
}
