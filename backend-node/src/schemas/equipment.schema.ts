/**
 * @openapi
 * components:
 *  schemas:
 *    Equipment:
 *      type: object
 *      properties:
 *        _id:
 *          type: string
 *          example: somerandomobjectidstring
 *        name:
 *          type: string
 *          example: Tester
 *        barcode:
 *          type: string
 *          example: barcodestring
 *        status:
 *          type: string
 *          example: defective
 *        defectiveSince:
 *          type: string
 *          example: April 24, 1996
 *    UpdateEquipmentInput:
 *      type: object
 *      properties:
 *        name:
 *          type: string
 *          example: Tester
 *        barcode:
 *          type: string
 *          example: barcodestring
 *        status:
 *          type: string
 *          example: defective
 *        defectiveSince:
 *          type: string
 *          example: April 24, 1996
 *    CreateEquipmentInput:
 *      type: object
 *      required:
 *        - name
 *        - barcode
 *      properties:
 *        name:
 *          type: string
 *          example: Tester
 *        barcode:
 *          type: string
 *          example: barcodestring
 *        status:
 *          type: string
 *          example: defective
 *        defectiveSince:
 *          type: string
 *          example: April 24, 1996
 */
