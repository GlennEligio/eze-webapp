/**
 * @openapi
 * components:
 *  schemas:
 *    TransactionEquipment:
 *      type: object
 *      properties:
 *        equipment:
 *          type: string
 *          required: true
 *          example: tester
 *        amount:
 *          type: integer
 *          minimum: 1
 *          required: true
 *          example: 2
 *    Transaction:
 *      type: object
 *      properties:
 *        _id:
 *          type: string
 *          example: somerandomobjectidstring
 *        equipments:
 *          type: array
 *          items:
 *            $ref: '#/components/schemas/TransactionEquipment'
 *          example: [{equipment: 'Tester', amount: 3}, {equipment: 'Voltmeter', amount: 2}]
 *        borrower:
 *          type: string
 *          example: studentid
 *        professor:
 *          type: string
 *          example: professorid
 *        borrowedAt:
 *          type: string
 *          format: date-time
 *          example: 2017-07-21T17:32:28Z
 *        returnedAt:
 *          type: string
 *          format: date-time
 *          example: 2017-07-21T17:32:28Z
 *        status:
 *          type: string
 *          example: accepted
 *    CreateTransactionInput:
 *      type: object
 *      required:
 *        - equipments
 *        - borrower
 *        - professor
 *        - borrowedAt
 *      properties:
 *        equipments:
 *          type: array
 *          items:
 *            $ref: '#/components/schemas/TransactionEquipment'
 *          example: [{equipment: 'Tester', amount: 3}, {equipment: 'Voltmeter', amount: 2}]
 *        borrower:
 *          type: string
 *          example: studentid
 *        professor:
 *          type: string
 *          example: professorid
 *        status:
 *          type: string
 *          example: accepted
 *          default: 'pending'
 *    UpdateTransactionInput:
 *      type: object
 *      properties:
 *        equipments:
 *          type: array
 *          items:
 *            $ref: '#/components/schemas/TransactionEquipment'
 *          example: [{equipment: 'Tester', amount: 3}, {equipment: 'Voltmeter', amount: 2}]
 *        borrower:
 *          type: string
 *          example: studentid
 *        professor:
 *          type: string
 *          example: professorid
 *        borrowedAt:
 *          type: string
 *          format: date-time
 *          example: 2017-07-21T17:32:28Z
 *        returnedAt:
 *          type: string
 *          format: date-time
 *          example: 2017-07-21T17:32:28Z
 *        status:
 *          type: string
 *          example: accepted
 */
