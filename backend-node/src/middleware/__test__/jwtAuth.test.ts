import { Request, Response } from "express";
import { CustomRequest } from "../../types/CustomRequest";
import httpMock from "node-mocks-http";
import jwtAuth from "../jwtAuth";
import event from "events";
import Account from "../../model/account";

describe("JwtAuth middleware", () => {
  let mockRequest: CustomRequest;
  let mockResponse: Response;
  const nextFunction = jest.fn();

  beforeEach = () => {
    mockResponse = httpMock.createResponse({
      eventEmitter: event.EventEmitter,
    });
  };

  test("allows request with /api/v1/login or /api/v1/register to pass", async () => {
    mockRequest = httpMock.createRequest();
    mockRequest.originalUrl = "/api/accounts/login";
    await jwtAuth(
      mockRequest as Request,
      mockResponse as Response,
      nextFunction
    );

    // Check if Auth middleware gave anonymous account
    expect(mockRequest.account).toMatchObject(
      new Account({
        fullname: "*",
        username: "*",
        password: "*",
        email: "anonymous@gmail.com",
        type: "*",
      })
    );
  });
});
