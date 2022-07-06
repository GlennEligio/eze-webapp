class ApiError extends Error {
  constructor(httpCode, message) {
    super(message);
    this.httpCode = httpCode;
  }
}

module.exports = ApiError;
