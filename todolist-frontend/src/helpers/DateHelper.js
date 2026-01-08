// src/helpers/DateHelper.js
import dayjs from "dayjs";

class DateHelper {
    /**
     * Format a date to "HH:mm:ss DD/MM/YYYY"
     * @param {string|Date|number} date
     * @returns {string} formatted date or "+" if invalid
     */
    static formatDate(date) {
        if (!date) return "+";
        return dayjs(date).format("HH:mm:ss DD/MM/YYYY");
    }

    /**
     * Optional: format to only date
     */
    static formatOnlyDate(date) {
        if (!date) return "+";
        return dayjs(date).format("DD/MM/YYYY");
    }

    /**
     * Optional: format to only time
     */
    static formatOnlyTime(date) {
        if (!date) return "+";
        return dayjs(date).format("HH:mm:ss");
    }

    static formatForServer(date){
        if (!date) return "+";
        return dayjs(date).format("YYYY-MM-DDTHH:mm:ss");

    }
}

export default DateHelper;
