import AntPathMatcher from "ant-path-matcher";
import ApiError from "../error/ApiError";
import express from "express";
import { CustomRequest } from "../types/CustomRequest";

const matcher = new AntPathMatcher();

class Rule {
  constructor(public method: string, public patterns: string[]) {
    this.method = method;
    this.patterns = patterns;
  }
}

class TypeRule {
  constructor(public type: string, public rules: Rule[]) {
    this.type = type;
    this.rules = rules;
  }
}

class AccountTypeConfig {
  private typeRules: TypeRule[];
  private static instance: AccountTypeConfig;

  private constructor() {
    this.typeRules = [];
  }

  public static getInstance() {
    if (this.instance) {
      return this.instance;
    } else {
      this.instance = new AccountTypeConfig();
      return this.instance;
    }
  }

  addTypeRule(type: string, method: string, ...patterns: string[]) {
    const typeIndex = this.typeRules.findIndex(
      (typeRule) => typeRule.type === type
    );
    if (typeIndex < 0) {
      this.typeRules.push(new TypeRule(type, [new Rule(method, patterns)]));
    } else {
      const typeRule = this.typeRules[typeIndex];
      this.typeRules[typeIndex].type = type;
      this.typeRules[typeIndex].rules = [
        ...typeRule.rules,
        new Rule(method, patterns),
      ];
    }
  }

  validate(type: string, method: string, url: string) {
    let isValid = false;
    this.typeRules.forEach((typeRule) => {
      if (matcher.match(typeRule.type, type)) {
        typeRule.rules.forEach((rule) => {
          if (rule.method.toLowerCase() === method.toLowerCase()) {
            rule.patterns.forEach((pattern) => {
              if (matcher.match(pattern, url)) {
                isValid = true;
              }
            });
          }
        });
      }
    });
    return isValid;
  }
}

const accountTypeConfig = AccountTypeConfig.getInstance();
accountTypeConfig.addTypeRule(
  "*",
  "POST",
  "/api/accounts/register",
  "/api/accounts/login"
);
accountTypeConfig.addTypeRule("*", "GET", "/api/docs/**");
accountTypeConfig.addTypeRule(
  "USER",
  "GET",
  "/api/transactions/*",
  "/api/equipments/*",
  "/api/professors/*",
  "/api/students/*",
  "/api/schedules/*"
);
accountTypeConfig.addTypeRule("USER", "POST", "/api/transactions/*");
accountTypeConfig.addTypeRule("ADMIN", "GET", "/**");
accountTypeConfig.addTypeRule("ADMIN", "POST", "/**");
accountTypeConfig.addTypeRule("ADMIN", "PATCH", "/**");
accountTypeConfig.addTypeRule("ADMIN", "DELETE", "/**");

const typeMiddleware: express.RequestHandler = async (
  req: CustomRequest,
  _res,
  next
) => {
  try {
    const path = req.originalUrl;
    const type = req.account!.type!;

    const isValid = accountTypeConfig.validate(type, req.method, path);
    if (!isValid) {
      throw new ApiError(403, "Account type not allowed to use the endpoint");
    }
    next();
  } catch (e) {
    next(e);
  }
};

export default typeMiddleware;
