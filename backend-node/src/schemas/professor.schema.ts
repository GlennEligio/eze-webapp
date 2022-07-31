/**
 * @openapi
 * components:
 *  schemas:
 *    Professor:
 *      type: object
 *      properties:
 *        _id:
 *          type: string
 *          example: somerandomobjectidstring
 *        name:
 *          type: string
 *          example: Tester
 *        contactNumber:
 *          type: string
 *          example: barcodestring
 *    CreateProfessorInput:
 *      type: object
 *      required:
 *        - name
 *        - contactNumber
 *      properties:
 *        _id:
 *          type: string
 *          example: somerandomobjectidstring
 *        name:
 *          type: string
 *          example: Tester
 *        contactNumber:
 *          type: string
 *          example: barcodestring
 *    UpdateProfessorInput:
 *      type: object
 *      properties:
 *        name:
 *          type: string
 *          example: Tester
 *        contactNumber:
 *          type: string
 *          example: barcodestring
 */
