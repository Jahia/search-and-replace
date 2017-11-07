// this function extends oSort for a specific pattern of date :
jQuery.extend(jQuery.fn.dataTableExt.oSort, {
    "date-eu-pre": function (a) {
        var euDatea = jQuery(a).text().split(',');
        if (euDatea.length == 2) {
            var day = euDatea[0];
            var restOfDate = jQuery(euDatea[1].split(' '));
            var month = restOfDate[1];
        }

        if (month == "january" || month == "January") {
            month = "01";
        }
        else if (month == "february" || month == "February") {
            month = "02";
        }
        else if (month == "march" || month == "March") {
            month = "03";
        }
        else if (month == "april" || month == "April") {
            month = "04";
        }
        else if (month == "may" || month == "May") {
            month = "05";
        }
        else if (month == "june" || month == "June") {
            month = "06";
        }
        else if (month == "july" || month == "July") {
            month = "07";
        }
        else if (month == "august" || month == "August") {
            month = "08";
        }
        else if (month == "september" || month == "September") {
            month = "09";
        }
        else if (month == "october" || month == "October") {
            month = "10";
        }
        else if (month == "november" || month == "November") {
            month = "11";
        }
        else {
            month = "12";
        }

        var time = restOfDate[3].split(':');
        return (restOfDate[2] + month + day + time[0] + time[1]) * 1;
    },

    "date-eu-asc": function (a, b) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },

    "date-eu-desc": function (a, b) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
});