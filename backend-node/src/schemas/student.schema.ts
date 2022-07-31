/**
 * @openapi
 * components:
 *  schemas:
 *    Student:
 *      type: object
 *      properties:
 *        _id:
 *          type: string
 *          example: somerandomobjectidstring
 *        studentNumber:
 *          type: string
 *          example: 2015-00129-MN-0
 *        fullname:
 *          type: string
 *          example: John Glenn Eligio
 *        yearAndSection:
 *          type: string
 *          example: BSECE 5-3
 *        contactNumber:
 *          type: string
 *          example: +63906256545
 *        birthday:
 *          type: string
 *          example: 04/30/45
 *        address:
 *          type: string
 *          example: UNIT C LOT 11
 *        email:
 *          type: string
 *          example: john@gmail.com
 *        guardian:
 *          type: string
 *          example: john doe sr.
 *        guardianNumber:
 *          type: string
 *          example: +639898989
 *        yearLevel:
 *          type: string
 *          example: 4th
 *    CreateStudentInput:
 *      type: object
 *      required:
 *        - studentNumber
 *        - fullname
 *        - yearAndSection
 *      properties:
 *        studentNumber:
 *          type: string
 *          example: 2015-00129-MN-0
 *        fullname:
 *          type: string
 *          example: John Glenn Eligio
 *        yearAndSection:
 *          type: string
 *          example: BSECE 5-3
 *        contactNumber:
 *          type: string
 *          example: +63906256545
 *        birthday:
 *          type: string
 *          example: 04/30/45
 *        address:
 *          type: string
 *          example: UNIT C LOT 11
 *        email:
 *          type: string
 *          example: john@gmail.com
 *        guardian:
 *          type: string
 *          example: john doe sr.
 *        guardianNumber:
 *          type: string
 *          example: +639898989
 *        yearLevel:
 *          type: string
 *          example: 4th
 *    UpdateStudentInput:
 *      type: object
 *      properties:
 *        studentNumber:
 *          type: string
 *          example: 2015-00129-MN-0
 *        fullname:
 *          type: string
 *          example: John Glenn Eligio
 *        yearAndSection:
 *          type: string
 *          example: BSECE 5-3
 *        contactNumber:
 *          type: string
 *          example: +63906256545
 *        birthday:
 *          type: string
 *          example: 04/30/45
 *        address:
 *          type: string
 *          example: UNIT C LOT 11
 *        email:
 *          type: string
 *          example: john@gmail.com
 *        guardian:
 *          type: string
 *          example: john doe sr.
 *        guardianNumber:
 *          type: string
 *          example: +639898989
 *        yearLevel:
 *          type: string
 *          example: 4th
 */
