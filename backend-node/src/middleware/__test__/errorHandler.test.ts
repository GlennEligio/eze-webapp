import ApiError from "../../error/ApiError";
import { NextFunction, Request, Response } from "express";
import httpMock from "node-mocks-http";
import errorHandler from "../errorHandler";
import events from "events";

describe("ErrorHandler middleware", () => {
  let mockRequest: Request;
  let mockResponse: any;
  let mockError: Error;
  let nextFunction: NextFunction = jest.fn();

  beforeEach(() => {
    mockRequest = httpMock.createRequest();
    mockResponse = httpMock.createResponse({
      eventEmitter: events.EventEmitter,
    });
  });

  test("should have same status code with the ApiError", () => {
    mockError = new ApiError(400, "Bad request");
    mockRequest.originalUrl = "/api/v1/login";
    const expectedResponse = {
      code: 400,
      message: "Bad request",
      path: "/api/v1/login",
    };
    mockResponse.on("finish", function () {
      // Assertion
      expect(mockResponse._getJSONData()).toMatchObject(expectedResponse);
    });

    errorHandler(
      mockError as ApiError,
      mockRequest as Request,
      mockResponse as Response,
      nextFunction as NextFunction
    );
  });

  test("should have status code 500 with base Error", () => {
    mockError = new Error("Internal Server Error");
    mockRequest.originalUrl = "/api/v1/login";
    const expectedResponse = {
      code: 500,
      message: "Internal Server Error",
      path: "/api/v1/login",
    };
    mockResponse.on("finish", function () {
      // Assertion
      expect(mockResponse._getJSONData()).toMatchObject(expectedResponse);
    });

    errorHandler(
      mockError,
      mockRequest as Request,
      mockResponse as Response,
      nextFunction as NextFunction
    );
  });
});
