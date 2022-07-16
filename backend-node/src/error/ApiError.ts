class ApiError extends Error {
  constructor(public httpCode: number, public message: string) {
    super(message);
    this.httpCode = httpCode;
  }
}

export default ApiError;
