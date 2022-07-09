const AntPathMatcher = require("ant-path-matcher");
const ApiError = require("../error/ApiError");
const matcher = new AntPathMatcher();

class AccountTypeConfig {
  constructor() {
    this.typeRules = [];
  }

  addTypeRule(type, method, ...patterns) {
    const typeIndex = this.typeRules.findIndex(
      (typeRule) => typeRule.type === type
    );
    if (typeIndex < 0) {
      this.typeRules.push({
        type,
        rules: [
          {
            method,
            patterns,
          },
        ],
      });
    } else {
      const typeRule = this.typeRules[typeIndex];
      this.typeRules[typeIndex] = {
        type,
        rules: [...typeRule.rules, { method, patterns }],
      };
    }
  }

  validate(type, method, url) {
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

const accountTypeConfig = new AccountTypeConfig();
accountTypeConfig.addTypeRule(
  "*",
  "POST",
  "/accounts/register",
  "/accounts/login"
);
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

const typeMiddleware = async (req, res, next) => {
  try {
    const path = req.originalUrl;
    const type = req.account.type;

    const isValid = accountTypeConfig.validate(type, req.method, path);
    if (!isValid) {
      throw new ApiError(403, "Account type not allowed to use the endpoint");
    }
    next();
  } catch (e) {
    next(e);
  }
};

module.exports = typeMiddleware;
