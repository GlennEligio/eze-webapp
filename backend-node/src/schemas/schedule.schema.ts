/**
 * @openapi
 * components:
 *  schemas:
 *    Schedule:
 *      type: object
 *      properties:
 *        _id:
 *          type: string
 *          example: somerandomobjectidstring
 *        subjectCode:
 *          type: string
 *          example: ECEN1012
 *        subjectName:
 *          type: string
 *          example: Engineering Economics
 *        day:
 *          type: string
 *          example: Wed
 *        time:
 *          type: string
 *          example: 10:30pm
 *        room:
 *          type: string
 *          example: 504A
 *        yearLevel:
 *          type: string
 *          example: 4th year
 *        yearAndSection:
 *          type: string
 *          example: 4-3
 *        professor:
 *          type: string
 *          example: professorobjectid
 *    CreateScheduleInput:
 *      type: object
 *      required:
 *        - subjectCode
 *        - subjectName
 *        - day
 *        - time
 *        - room
 *        - yearLevel
 *        - yearAndSection
 *        - professor
 *      properties:
 *        subjectCode:
 *          type: string
 *          example: ECEN1012
 *        subjectName:
 *          type: string
 *          example: Engineering Economics
 *        day:
 *          type: string
 *          example: Wed
 *        time:
 *          type: string
 *          example: 10:30pm
 *        room:
 *          type: string
 *          example: 504A
 *        yearLevel:
 *          type: string
 *          example: 4th year
 *        yearAndSection:
 *          type: string
 *          example: 4-3
 *        professor:
 *          type: string
 *          example: professorobjectid
 *    UpdateScheduleInput:
 *      type: object
 *      properties:
 *        subjectCode:
 *          type: string
 *          example: ECEN1012
 *        subjectName:
 *          type: string
 *          example: Engineering Economics
 *        day:
 *          type: string
 *          example: Wed
 *        time:
 *          type: string
 *          example: 10:30pm
 *        room:
 *          type: string
 *          example: 504A
 *        yearLevel:
 *          type: string
 *          example: 4th year
 *        yearAndSection:
 *          type: string
 *          example: 4-3
 *        professor:
 *          type: string
 *          example: professorobjectid
 */
