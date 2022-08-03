/**
 * @openapi
 * components:
 *  schemas:
 *    AccountToken:
 *      type: object
 *      required:
 *        - token
 *      properties:
 *        _id:
 *          type: string
 *          example: tokenid
 *        token:
 *          type: string
 *          required: true
 *          example: eyasjajskqweqwe.qweqwe1231.qwe1231231
 *    Account:
 *      type: object
 *      properties:
 *        _id:
 *          type: string
 *          example: somerandomobjectidstring
 *        fullname:
 *          type: string
 *          required: true
 *          example: John Doe
 *        username:
 *          type: string
 *          required: true
 *          example: johndoe
 *        email:
 *          type: string
 *          format: email
 *          required: true
 *          example: johndoe@gmail.com
 *        type:
 *          type: string
 *          required: true
 *          example: USER
 *        createdAt:
 *          type: string
 *          format: date-time
 *          example: 2017-07-21T17:32:28Z
 *        updatedAt:
 *          type: string
 *          format: date-time
 *          example: 2017-07-21T17:32:28Z
 *    CreateAccountInput:
 *      type: object
 *      required:
 *        - fullname
 *        - username
 *        - email
 *        - password
 *        - type
 *      properties:
 *        fullname:
 *          type: string
 *          required: true
 *          example: John Doe
 *        username:
 *          type: string
 *          required: true
 *          example: johndoe
 *        email:
 *          type: string
 *          format: email
 *          required: true
 *          example: johndoe@gmail.com
 *        password:
 *          type: string
 *          required: true
 *          example: johndoe
 *        type:
 *          type: string
 *          required: true
 *          default: USER
 *    UpdateAccountInput:
 *      type: object
 *      properties:
 *        fullname:
 *          type: string
 *          required: true
 *          example: John Doe
 *        username:
 *          type: string
 *          required: true
 *          example: johndoe
 *        email:
 *          type: string
 *          format: email
 *          required: true
 *          example: johndoe@gmail.com
 *        password:
 *          type: string
 *          required: true
 *          example: johndoe
 *        type:
 *          type: string
 *          required: true
 *          example: USER
 *    RegisterAccountInput:
 *      type: object
 *      required:
 *        - fullname
 *        - username
 *        - email
 *        - password
 *      properties:
 *        fullname:
 *          type: string
 *          required: true
 *          example: John Doe
 *        username:
 *          type: string
 *          required: true
 *          example: johndoe
 *        email:
 *          type: string
 *          required: true
 *          format: email
 *          example: johndoe@gmail.com
 *        password:
 *          type: string
 *          required: true
 *          example: johndoe
 *    LoginAccountInput:
 *      type: object
 *      required:
 *        - username
 *        - password
 *      properties:
 *        username:
 *          type: string
 *          required: true
 *          example: johndoe
 *        password:
 *          type: string
 *          required: true
 *          example: johndoe
 *    LoginRegisterAccountOutput:
 *      type: object
 *      properties:
 *        _id:
 *          type: string
 *          example: somerandomobjectidstring
 *        fullname:
 *          type: string
 *          required: true
 *          example: John Doe
 *        username:
 *          type: string
 *          required: true
 *          example: johndoe
 *        email:
 *          type: string
 *          format: email
 *          required: true
 *          example: johndoe@gmail.com
 *        type:
 *          type: string
 *          required: true
 *          example: USER
 *        createdAt:
 *          type: string
 *          format: date-time
 *          example: 2017-07-21T17:32:28Z
 *        updatedAt:
 *          type: string
 *          format: date-time
 *          example: 2017-07-21T17:32:28Z
 *        token:
 *          $ref: '#/components/schemas/AccountToken'
 */
