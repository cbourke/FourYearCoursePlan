/**
 * Department of Computer Science & Engineering 
 * University of Nebraska - Lincoln
 * 4 Year Course Planner Application
 * 
 * Chris Bourke
 * cbourke@cse.unl.edu
 */

var cse = {
    version : "0.2.0",
    courses : [],
    schedule : null,
    transferTermCode : 1000,
    currentSemester : 1158,
    /**
     * Adds a term to the schedule
     * @param termCode
     * @returns {Boolean}
     */
    addTerm: function (tc) {
        //console.log("adding term...");
        var termCode = parseInt(tc, 10);
        if (termCode === cse.transferTermCode) {
            //console.log("adding transfer term to schedule...");
            if (!cse.schedule.transferCourses) {
                cse.schedule.transferCourses = [];
            } else {
                //console.log("transfer term already present");
                return false;
            }
        } else if (this.schedule.termCodes.length === 0) {
            //console.log("adding term " + termCode);
            this.schedule.termCodes.push(termCode.toString());
            this.schedule[termCode.toString()] = [];
        } else if (this.schedule.termCodes.indexOf(termCode.toString()) !== -1) {
            //console.log("schedule already contains term "+termCode);
            return false;
        } else {
            //console.log("adding range...");
            var newTerms = [],
                start = Math.min(termCode, parseInt(this.schedule.termCodes[0], 10)),
                end   = Math.max(termCode, parseInt(this.schedule.termCodes[this.schedule.termCodes.length - 1], 10)),
                term,
                i;

            for (term = start; term <= end; term = cse.termCodeCalc(term, cse.termCodeOffsets.NEXT)) {
                //if summer, only include if  current schedule contains it
                if (term % 10 !== 6 || cse.schedule.termCodes.indexOf(term.toString()) > -1 || term === termCode) {
                    newTerms.push(term.toString());
                }
            }
            cse.schedule.termCodes = newTerms;
            for (i = 0; i < newTerms.length; i++) {
                if (!cse.schedule[newTerms[i]]) {
                    cse.schedule[newTerms[i]] = [];
                }
            }
        }
        //console.log(cse.schedule);
        cse.setSchedule(cse.schedule);
        return true;
    },
    trimTerms : function () {
        var removeTerms = [],
            i;
        if(cse.schedule.transferCourses && cse.schedule.transferCourses.length == 0) {
            delete cse.schedule.transferCourses;
        }
        for (i = 0; i < cse.schedule.termCodes.length && cse.schedule[cse.schedule.termCodes[i]].length === 0; i++) {
            removeTerms.push(cse.schedule.termCodes[i]);
        }
        for (i = cse.schedule.termCodes.length-1; i>=0 && cse.schedule[cse.schedule.termCodes[i]].length === 0; i--) {
            if (removeTerms.indexOf(cse.schedule.termCodes[i]) === -1) {
                removeTerms.push(cse.schedule.termCodes[i]);
            }
        }
        for(i = 0; i < cse.schedule.termCodes.length; i++) {
            if(cse.schedule.termCodes[i] % 10 == 6 && cse.schedule[cse.schedule.termCodes[i]].length == 0) {
                if(removeTerms.indexOf(cse.schedule.termCodes[i]) == -1) {
                    removeTerms.push(cse.schedule.termCodes[i]);
                }
            }
        }
        //console.log("remove Terms: " + removeTerms);        
        for(var i=0; i<removeTerms.length; i++) {
            cse.schedule.termCodes.splice(cse.schedule.termCodes.indexOf(removeTerms[i]), 1);
            delete cse.schedule[removeTerms[i]];
        }
        cse.setSchedule(cse.schedule);
    },
    termCodeOffsets : {
        NEXT : 1, PREVIOUS : 2
    },
    termCodeCalc : function(termCode, type) {
        termCode = parseInt(termCode);
        if(type === this.termCodeOffsets.PREVIOUS) {
            if(termCode % 10 == 8) {
                return termCode - 2;
            } else if(termCode % 10 == 1) {
                return termCode - 3;
            } else if(termCode % 10 == 6) {
                return termCode - 5;
            } else {
                return null;
            }
        } else if(type === this.termCodeOffsets.NEXT) {
            if(termCode % 10 == 8) {
                return termCode + 3;
            } else if(termCode % 10 == 1) {
                return termCode + 5;
            } else if(termCode % 10 == 6) {
                return termCode + 2;
            } else {
                return null;
            }
        } else {
            return null;
        }
    },
    advisorLoad : function () {
        var user = $( "#userLogin" ).val();
        //console.log('loading schedule for user ' + user);
        $( "#studentSearch" ).val('');
        $( "#userLogin" ).val('');
        cse.user = user;
        cse.loadRemote();
        $( ".currentUser" ).text(cse.user);
    },
    createEmptySchedule : function(startTermCode) {
        var term = startTermCode;
        var sched = {
            "termCodes" : []
        };
        for(var i=0; i<8; i++) {
            sched.termCodes.push(term.toString());
            sched[term.toString()] = [];
            (term % 2 == 0) ?  (term += 3) : (term += 7);
        }
        return sched;
    },
    setCourses : function(data) {
        //console.log("setting up courses...");
        this.coursesByUiGroup = data;
        $.each(data, function(key, val) {
              $.each(val, function(key, val) {
                  cse.courses.push(val);
              });
        });
        this.courses.sort(function(a, b) {
          return a.courseId.localeCompare(b.courseId);
        });
    },
    addCourse : function(course) {
        var c = this.getCourseById(course.courseId);
        if(c != null) {
            //console.log("WARNING: attempted to addCourse "+course.courseId+" but it already exists");
            return false;
        }
        else {
            //console.log("adding course "+course.courseId+" to cse.courses...");
            var index = 0;
            while(cse.courses[index].courseId.localeCompare(course.courseId) < 0) {
                index++;
            }
            cse.courses.splice(index, 0, course);
            return true;
        }
    },
    getCourseById : function(courseId) {
        var low = 0, high = this.courses.length - 1, i, comparison;
        while (low <= high) {
            i = Math.floor((low + high) / 2);
            comparison = this.courses[i].courseId.localeCompare(courseId);
            if (comparison < 0) { low = i + 1; continue; }
            else if (comparison > 0) { high = i - 1; continue; }
            else { return this.courses[i]; }
        }
        return null;
    },
    setSchedule: function(sched) {
        //validate the schedule: if it begins in a spring semester, add leading fall semester for the sake of the UI
        if(sched.termCodes[0] % 10 == 1) {
            sched.termCodes.unshift( (sched.termCodes[0]-3).toString() );
            sched[sched.termCodes[0].toString()] = [];
        }
        //if it ends in a fall semester, add a trailing spring semester
        if(sched.termCodes[sched.termCodes.length-1] % 10 == 8) {
            sched.termCodes.push( (parseInt(sched.termCodes[sched.termCodes.length-1])+3).toString() );
            sched[sched.termCodes[sched.termCodes.length-1].toString()] = [];
        }
        this.schedule = sched;
        cse.setOptions();
        cse.ui.initializeSchedule();
    },    
    setOptions : function() {
        $("input[name='degreeType'][value='"+this.schedule.degreeTypeId+"']").prop('checked',true);
    },
    scheduleCourse : function(courseId, termCode) {
        var course = cse.getCourseById(courseId);
        if(course == null) {
            //console.log("Warning: course, "+courseId + " not recognized");
            return; 
        }
        cse.unscheduleCourse(courseId);
        //console.log("adding "+course.courseId+" to "+termCode);
        if(termCode == cse.transferTermCode) {
            this.schedule.transferCourses.push(courseId);
        } else {
            this.schedule[termCode.toString()].push(courseId);
        }
    },
    unscheduleCourse : function(courseId) {
        //if its a transfer course:
        if(cse.schedule.transferCourses && cse.schedule.transferCourses.indexOf(courseId) > -1) {
            //console.log("removing "+ courseId+ " from transfer credits");
            cse.schedule.transferCourses.splice(cse.schedule.transferCourses.indexOf(courseId), 1);
        } else {
            for(var i=0; i<this.schedule.termCodes.length; i++) {
                var tCode = this.schedule.termCodes[i].toString();
                var term = this.schedule[tCode];
                for(var j=0; j<term.length; j++) {
                    //if the term contains the course, remove it
                    if(term[j] == courseId) {
                        //console.log("removing "+ courseId+ " from "+tCode);
                        term.splice(j, 1);
                    }
                }
            }
        }
    },
    getPdf : function() {
        var form = "<form action='./pdfSchedule' method='post'>" +
                   "<input type='hidden' name='schedule' value='" +
                   JSON.stringify(cse.schedule) +
                   "'/>";
        if(cse.user) {
            form += "<input type='hidden' name='user' value='" + cse.user + "'/>";
        }
        form += "</form>";
        $(form).appendTo('body').submit().remove();
    },
    saveLocal : function() {
        $('#informationDiv').html('');
        localStorage.setItem('schedule', JSON.stringify(this.schedule));
        cse.ui.addAlertDiv(cse.ui.alertValues.SUCCESS, "Schedule successfullly saved to local storage.", $('#informationDiv'));
    },
    saveRemote : function () {
        $('#informationDiv').html('<img src="./img/loading48x48-trans.gif" style="margin-left: 1em; margin-top: 1em;" alt="loading"/>');
        var data = {
            schedule : JSON.stringify(this.schedule)
        };
        if(cse.user) {
          data.user = cse.user;
        }
        $.ajax({
            type: "POST",
            url: "./secure/saveSchedule",
            data: data,
            success: function(data) {
                //console.log("saveRemote success");
                if(data.status === "success") {
                    cse.ui.addAlertDiv(cse.ui.alertValues.SUCCESS, "Schedule successfullly saved to database.", $('#informationDiv'));
                } else {
                    this.error();
                }
            },
            error: function() {
                cse.ui.addAlertDiv(cse.ui.alertValues.ERROR, "Unable to save schedule to database.", $('#informationDiv'));
            }
        });    
    },
    loadLocal : function() {
        $('#informationDiv').html('');
        cse.setSchedule(JSON.parse(localStorage.getItem('schedule')));
        cse.ui.addAlertDiv(cse.ui.alertValues.SUCCESS, "Schedule successfullly loaded from local storage.", $('#informationDiv'));
    },
    loadRemote : function () {
        $('#informationDiv').html('<img src="./img/loading48x48-trans.gif" style="margin-left: 1em; margin-top: 1em;" alt="loading"/>');
        var data = {};
        if(cse.user) {
          data = {
            user :  cse.user
          };
        }
        $.ajax({
            //type: "POST",
            url: "./secure/loadSchedule",
            data: data,
            success: function(data) {
                if(data.termCodes) {
                    cse.setSchedule(data);
                    cse.ui.addAlertDiv(cse.ui.alertValues.SUCCESS, "Schedule successfullly loaded from the database.", $('#informationDiv'));
                } else {
                    cse.loadNewEmptySchedule();
                    cse.ui.addAlertDiv(cse.ui.alertValues.SUCCESS, "No schedule has yet been saved to the database.", $('#informationDiv'));
                }
            },
            error: function() {
                cse.ui.addAlertDiv(cse.ui.alertValues.ERROR, "Unable to load schedule from database.", $('#informationDiv'));
            }
        });    
        
    },
    ui: {
        courseElementPrefix: "ui-course-",
        scheduledCourseElementPrefix: "ui-scheduled-course-",
        termElementPrefix: "ui-term-",
        monthToTermMap : {
            1 : "Spring",
            6 : "Summer",
            7 : "Summer",
            8 : "Fall"
        },
        scheduleMap: {
            "fall,spring,even,odd" : "Every fall and spring semester",
            "fall,spring,summer,even,odd" : "Every fall, spring, and summer semester",
            "fall,even,odd" : "Every fall semester",
            "spring,even,odd" : "Every spring semester",
            "fall,odd" : "Every fall semester in an odd year",
            "fall,even" : "Every fall semester in an even year",
            "spring,odd" : "Every spring semester in an odd year",
            "spring,even" : "Every spring semester in an even year"
        },
        cloneHelper: function() {
            return $(this).clone().width($(this).width());
        },
        addTerm : function() {
            var e = document.getElementById("newTermSem");
            var sem = parseInt(e.options[e.selectedIndex].value);
            var year = parseInt($('#newTermYear').val());
            var term = 1000 + (year % 100) * 10 + sem;               
            console.log("adding term "+term+"...");
            var result = cse.addTerm(term);
            if(result) {
                cse.ui.addAlertDiv(cse.ui.alertValues.SUCCESS, "Successfully added term(s), you may now schedule courses.", $('#informationDiv'));
            } else {
                cse.ui.addAlertDiv(cse.ui.alertValues.ERROR, "Did not add term(s), they were already part of your schedule.", $('#informationDiv'));
            }
        },
        addCourse : function(courseId) {
            var course = cse.getCourseById(courseId);
            if(!course) {
                console.log("attempted to add course "+courseId+" to UI but it does not exist in cse.courses");
                return;
            }
            //if UI element already exists, 
            if($("#"+this.courseElementPrefix+course.courseId).length > 1) {
                console.log("attempted to add course UI for course "+courseId+" but it already exists");
                return;
            }
            var courseDiv = cse.ui.createCourseDiv(course);
            $(courseDiv).hide();
            if(course.uiGroup) {
                //uiGroup is specified, so put it where it belongs
                var headerTrimmed = course.uiGroup.replace(/\s+/g, '');
                $('#courseMenuGroup-'+headerTrimmed+'-list').append(courseDiv);
            } else {            
                $('#customCourseList').append(courseDiv);
            }
            $(courseDiv)
                .show('fade')
                .draggable({
                    helper: cse.ui.cloneHelper, 
                    revert: "invalid"
            });
        },
        scheduleCourse : function(courseId, termCode) {
            var course = cse.getCourseById(courseId);
            if(course == null) {
                console.log("WARNING: courseId = " + courseId + " invalid");
                return;
            }
            //disable the original course menu item ...
            $('#'+cse.ui.courseElementPrefix+courseId).draggable('disable');

            //check that it doesn't already exist
            if($('#'+cse.ui.scheduledCourseElementPrefix+courseId).length > 0) {
                console.log(cse.ui.scheduledCourseElementPrefix+courseId + ' exists, removing...');
                //if it exists, remove it, recreate it
                $('#'+cse.ui.scheduledCourseElementPrefix+courseId).remove();
            }
            
            console.log(courseId + " ui element dropped to "+termCode);

            cse.ui.createCourseDiv(course)
                .attr('id', cse.ui.scheduledCourseElementPrefix+courseId)
                .appendTo($('#'+cse.ui.termElementPrefix+termCode+' .termCourseList'))
                .hide()
                .show('fade')
                .draggable({
                    helper: cse.ui.cloneHelper, 
                    revert: "invalid"
                });
            
            cse.ui.updateCreditHours();
        },
        unscheduleCourse : function(courseId) {
            if($('#'+cse.ui.scheduledCourseElementPrefix+courseId).length > 0) {
                console.log(cse.ui.scheduledCourseElementPrefix+courseId + ' exists, removing...');
                //if it exists, remove it, recreate it
                $('#'+cse.ui.scheduledCourseElementPrefix+courseId).remove();
            }
            $('#'+cse.ui.courseElementPrefix+courseId).draggable('enable').hide().show('highlight');
            cse.ui.updateCreditHours();
        },
        updateCreditHours : function() {
            //update all credit hours along with a total
            var grandTotal = 0;
            if(cse.schedule.transferCourses && cse.schedule.transferCourses.length > 0) {
                console.log("updating transfer credit hours");
                var total = 0;
                var courseIds = cse.schedule.transferCourses;
                for(var j=0; j<courseIds.length; j++) {
                    var course = cse.getCourseById(courseIds[j]);
                    if(course == null) {
                        console.log("Warning: course "+courseIds[j]+" does not exist");
                    } else {
                        total += parseInt(course.creditHours);
                    }
                }
                $('#'+cse.ui.termElementPrefix+cse.transferTermCode+' .creditHours').text("Credit Hours: "+total);
                grandTotal += total;
            } else {
                $('#'+cse.ui.termElementPrefix+cse.transferTermCode+' .creditHours').text("Credit Hours: 0");
            }
            for(var i=0; i<cse.schedule.termCodes.length; i++) {
                var termCode = cse.schedule.termCodes[i].toString();
                var courseIds = cse.schedule[termCode];
                var total = 0;
                for(var j=0; j<courseIds.length; j++) {
                    var course = cse.getCourseById(courseIds[j]);
                    if(course == null) {
                        console.log("Warning: course "+courseIds[j]+" does not exist");
                    } else {
                        total += parseInt(course.creditHours);
                    }
                }
                $('#'+cse.ui.termElementPrefix+termCode+' .creditHours').text("Credit Hours: "+total);
                grandTotal += total;
            }
            $('#totalCreditHours').text("Total Credit Hours: "+grandTotal);
        },
        scheduleToText : function(csv) {
            if(!csv || csv == "") {
                return "Unknown";
            } else {
                return this.scheduleMap[csv];
            }
        },
        termCodeToText : function(termCode) {
            if(termCode === cse.transferTermCode) {
                return "Transfer Credits";
            }
            var month = (termCode) % 10;
            var year  = ((termCode - month) / 10) % 100;
            return cse.ui.monthToTermMap[month] + " " + (year < 10 ? "200" : "20") + year;
        },
        alertValues : {
            SUCCESS : ['alert-success', 'label-success', 'Success!'],
            ERROR   : ['alert-error', 'label-important', 'Error!'],
            WARN    : [],
            INFO    : ['alert-info', 'label-info', 'Info']
        },
        addAlertDiv : function(typeVals, text, element) {
            var alertDiv = $('<div/>')
                .addClass('alert')
                .addClass(typeVals[0])
                .html('<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                        '<span class="label '+typeVals[1]+'">'+typeVals[2]+'</span> ' + text);
            $(element)
              .html('')
              .append(alertDiv);
            alertDiv.hide().show('fade', {}, 1000);

        },
        createCourseGroup: function(header, courses) {
            var headerTrimmed = header.replace(/\s+/g, '');
            var collapseIcon = $('<span/>')
                .addClass('ui-accordion-header-icon')
                .addClass('ui-icon')
                .addClass('ui-icon-triangle-1-e')
                .css('float', 'left');
            var courseGroupDivHeader = $("<h5/>")
                .addClass('ui-widget-header')
                .addClass('ui-corner-all')
                .addClass('collapsable')
                .css('margin', 0)
                .css('font-size', '10pt')
                .text(header)
                .prepend(collapseIcon);
            var courseGroupDivList = $("<div/>")
                .attr('id', 'courseMenuGroup-'+headerTrimmed+'-list')
                .hide();
            var courseGroupDiv = $("<div></div>")
                .attr('id', 'courseMenuGroup-'+headerTrimmed)
                .addClass('ui-widget-content')
                .addClass('ui-corner-all')
                .addClass('courseList')
                .append(courseGroupDivHeader)
                .append(courseGroupDivList);
            for(var i=0; i<courses.length; i++) {
                courseGroupDivList.append(this.createCourseDiv(courses[i]));
            }
            return courseGroupDiv;
        },
        toParagraph : function(text) {
            if(!text || text === '') {
                return '';
            } else {
                return '<p>' + text + '</p>';
            }
        },
        createCourseDetailDiv : function(course) {
            var content = '<h4>' + course.subject + ' ' + course.number + '</h4>' + 
                          '<p>' + course.title + '</p>' + 
                          '<p>Description: ' + course.description + '</p>' +
                          '<p>Credit Hours: ' + course.creditHours + '</p>' +
                          '<p>Schedule: ' + this.scheduleToText(course.schedule) + '</p>';
            if(course.prerequisiteText) {
                content += '<p>Prerequisites: ' + course.prerequisiteText + '</p>';
            }
            if(course.aces) {
                content += '<p>Satisfies ACE Outcome(s): '+ course.aces +'</p>';
            }
            if(course.advisorComment) {
                content += '<p>Adivser Comment: ' + course.advisorComment + '</p>';
            }
            return content;
        },
        createCourseDiv: function(course) {
            //info tool tip:
            var courseDraggableIcon = $("<img/>")
                .attr('src', './img/draggable16x16-trans.gif')
                .attr('alt', 'Drag me!')
                .css('float', 'right')
                .css('margin', '0px 0px 0px 5px')
                .css('cursor', 'move');
            var courseInfoIcon = $("<img/>")
                .attr('src', './img/info16x16-trans.png')
                .attr('alt', 'Click me')
                .css('float', 'right')
                .css('cursor', 'pointer');
            //qtip instead
            $(courseInfoIcon).qtip({
                content: {
                    text: this.createCourseDetailDiv(course),
                    title: {
                        text: 'Course Details',
                        button: true
                    }
                },
                position: {
                    at: 'right center', // Position the tooltip above the link
                    my: 'top left',
                    viewport: $(window), // Keep the tooltip on-screen at all times
                    effect: false // Disable positioning animation
                },
                show: {
                    event: 'click',
                    solo: true // Only show one tooltip at a time
                },
                hide: 'unfocus',
                style: {
                    classes: 'qtip-wiki qtip-light qtip-shadow'
                }
            });
            
            var header = $("<h5/>")
                .css('margin', '0em')
                .css('padding', '0px')
                .text(course.subject+" "+course.number);
//            var courseTitle = $("<p/>")
//                .css('margin', '0em')
//                .css('padding', '0px')
//                .css('font-size', 'x-small')
//                .text(course.title);
            var courseDiv = $('<div/>')
                .attr('id', this.courseElementPrefix+course.courseId)
                .attr('courseId', course.courseId)
                .addClass('ui-state-default')
                .addClass('ui-corner-all')
                .addClass('draggable')
                .css('margin', '0.5em')
                .css('padding', '3px')
                .append(courseDraggableIcon)
                .append(courseInfoIcon)
                .append(header);
//                .append(courseTitle);
            return courseDiv;
        },
        initializeCourseMenu: function () {
            console.log("initializing course menu...");
            $('#courseMenu').html('');
            $.each(cse.coursesByUiGroup, function(key, val) {
                var foo = cse.ui.createCourseGroup(key, val);
                $('#courseMenu').append(foo);
            });
            
            //hackishly expand the "first" group (hardcoded)
            $('#courseMenuGroup-CoreCSCECourses-list').show('blind', {}, 2000);
            $('#courseMenuGroup-CoreCSCECourses h5.collapsable span').toggleClass('ui-icon-triangle-1-e ui-icon-triangle-1-s');

            //make each group sortable
            $('#courseMenu').sortable({ containment : "parent" });
            //make each group collapsable
            $(".collapsable").click(function() {
                $('span', this).toggleClass('ui-icon-triangle-1-e ui-icon-triangle-1-s');
                $(this).next().toggle('blind', {}, 500);
            });
            //make each course list item draggable
            $(".courseList .draggable").draggable({
                helper: cse.ui.cloneHelper, 
                revert: "invalid"
            });
            //make the course menu droppable to restore unscheduled courses
            $("#courseMenuContainer").droppable({
                drop: function( event, ui ) {
                    var courseId = ui.draggable.attr('id').split('-').slice(-1)[0];
                    cse.unscheduleCourse(courseId);
                    cse.ui.unscheduleCourse(courseId);
                }
            });
        },
        createTerm : function(termCode) {
            var header = $('<h4>'+cse.ui.termCodeToText(termCode)+'</h4>')
                .css('margin', '0.25em');
            var list = $('<div/>')
                .addClass('ui-corner-all')
                .addClass('termCourseList');
            var hoursDiv = $('<div/>')
                .addClass('creditHours')
                .text("Credit Hours: 0");
            var term = $('<div/>')
                .attr('id', cse.ui.termElementPrefix+termCode)
                .addClass('ui-corner-all')
                .addClass('span6')
                .addClass('termDiv')
                .append(header)
                .append(list)
                .append(hoursDiv);
            return term;
        },
        initializeSchedule : function() {
            //reset courseMenu draggables to be enabled
            $(".draggable").draggable('enable');
            //reset total credit hours
            $('#totalCreditHours').text("Total Credit Hours: 0");
            //promise: termCodes begin in a summer or fall semester; every fall semester has a corresponding spring semester
            $('#scheduleDiv').html('');
            if(cse.schedule.transferCourses) {
                //full row with termDiv:
                var transferDiv = cse.ui.createTerm(cse.transferTermCode);
                var row = $('<div/>')
                    .attr('id', 'academicYear'+cse.transferTermCode)
                    .addClass('row-fluid')
                    .append(transferDiv)
                    .hide();
                $('#scheduleDiv').append(row);
                row.show('fade', {}, 1000);
                var courseIds = cse.schedule.transferCourses;
                for(var j=0; j<courseIds.length; j++) {
                    //do the check here to prevent the UI warning
                    var courseSubject = courseIds[j].substring(0,4);
                    var courseNumber  = courseIds[j].substring(4);
                    if(cse.getCourseById(courseSubject+courseNumber) == null) {
                        cse.addBulletinCourse(courseSubject, courseNumber, cse.transferTermCode);
                    } else {
                        cse.ui.scheduleCourse(courseIds[j], cse.transferTermCode);
                    }
                }

            }
            for(var i=0; i<cse.schedule.termCodes.length; i++) {

                if(cse.schedule.termCodes[i] % 10 == 6) {
                    //full row with termDiv:
                    var summerDiv = cse.ui.createTerm(cse.schedule.termCodes[i]);
                    var row = $('<div/>')
                        .attr('id', 'academicYear'+cse.schedule.termCodes[i])
                        .addClass('row-fluid')
                        .append(summerDiv)
                        .hide();
                    $('#scheduleDiv').append(row);
                    row.show('fade', {}, 1000);

                    var summerTermCode = cse.schedule.termCodes[i];
                    var courseIds = cse.schedule[summerTermCode];
                    for(var j=0; j<courseIds.length; j++) {
                        //do the check here to prevent the UI warning
                        var courseSubject = courseIds[j].substring(0,4);
                        var courseNumber  = courseIds[j].substring(4);
                        if(cse.getCourseById(courseSubject+courseNumber) == null) {
                            cse.addBulletinCourse(courseSubject, courseNumber, summerTermCode);
                        } else {
                            cse.ui.scheduleCourse(courseIds[j], summerTermCode);
                        }
                    }
                    
                } else {
                    var fallTermCode = cse.schedule.termCodes[i];
                    var fall = cse.ui.createTerm(fallTermCode);
                    var springTermCode = cse.schedule.termCodes[i+1];
                    var spring = cse.ui.createTerm(springTermCode);
                    var row = $('<div/>')
                        .attr('id', 'academicYear'+cse.schedule.termCodes[i])
                        .addClass('row-fluid')
                        .append(fall)
                        .append(spring)
                        .hide();
                    $('#scheduleDiv').append(row);
                    row.show('fade', {}, 1000);
                    
                    var fallCourseIds = cse.schedule[fallTermCode.toString()];
                    for(var j=0; j<fallCourseIds.length; j++) {
                        //do the check here to prevent the UI warning
                        var courseSubject = fallCourseIds[j].substring(0,4);
                        var courseNumber  = fallCourseIds[j].substring(4);
                        if(cse.getCourseById(courseSubject+courseNumber) == null) {
                            cse.addBulletinCourse(courseSubject, courseNumber, fallTermCode);
                        } else {
                            cse.ui.scheduleCourse(fallCourseIds[j], fallTermCode);
                        }
                    }
                    var springCourseIds = cse.schedule[springTermCode.toString()];
                    for(var j=0; j<springCourseIds.length; j++) {
                        var courseSubject = springCourseIds[j].substring(0,4);
                        var courseNumber  = springCourseIds[j].substring(4);
                        if(cse.getCourseById(courseSubject+courseNumber) == null) {
                            cse.addBulletinCourse(courseSubject, courseNumber, springTermCode);
                        } else {
                            cse.ui.scheduleCourse(springCourseIds[j], springTermCode);
                        }
                    }
                    i++; //skip to next semester
                }
            }

            $(".termDiv").droppable({
                activeClass: "ui-state-default",
                hoverClass: "ui-state-hover",
                accept: ":not(.ui-sortable-helper)",
                drop: function( event, ui ) {
                    var courseId = ui.draggable.attr('id').split('-').slice(-1)[0];
                    var termCode = event.target.id.split('-').slice(-1)[0];
                    cse.scheduleCourse(courseId, termCode);
                    cse.ui.scheduleCourse(courseId, termCode);
                }
            });
        }//end cse.ui
    },
    getParameters : function() {
        var match,
            pl     = /\+/g,  // Regex for replacing addition symbol with a space
            search = /([^&=]+)=?([^&]*)/g,
            decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
            query  = window.location.search.substring(1);

        var urlParams = {};
        while (match = search.exec(query)) {
           urlParams[decode(match[1])] = decode(match[2]);
        }
        return urlParams;
    },
    updateOptions : function() {
        this.schedule.degreeTypeId = $("input[name='degreeType']:checked").attr('value');
    },
    loadNewEmptySchedule : function(customTerm) {
        var beginTerm = 0;
        if(customTerm) {
            console.log("loading empty schedule for custom term...");
            var e = document.getElementById("newScheduleSem");
            var sem = parseInt(e.options[e.selectedIndex].value);
            var year = parseInt($('#newScheduleYear').val());
            beginTerm = 1000 + (year % 100) * 10 + sem;               
        } else if(cse.schedule && cse.schedule.termCodes) {
            console.log("loading empty schedule for current schedule term...");
            beginTerm = cse.schedule.termCodes[0];
        } else {
            console.log("loading empty schedule default currentSemester...");
            beginTerm = cse.currentSemester;
        }
        console.log("loading empty schedule begining "+beginTerm);
        beginTerm = parseInt(beginTerm);
        var newSchedule = cse.createEmptySchedule(beginTerm);
        console.log(newSchedule);
        cse.setSchedule(newSchedule);
    },
    buildSchedule : function() {
            params = cse.getParameters();
            $.getJSON('./buildSchedule', params, function(data) {
                cse.setSchedule(data);
                cse.ui.addAlertDiv(cse.ui.alertValues.SUCCESS, "Successfullly built your schedule now you can save, customize, print, etc.!  Be sure to meet with an academic advisor if you have any questions!", $('#informationDiv'));
            }).fail(function() {
                console.log("Loading of schedule failed");
                $('#scheduleDiv')
                    .hide()
                    .addClass("alert")
                    .html('<strong>Error!</strong> An unexpected error occurred, sorry.')
                    .show('bounce', {}, 1500);
            });
    },
    validateSchedule : function () {
        $('#validationDiv').html('<img src="./img/loading48x48-trans.gif" style="margin-left: 1em; margin-top: 1em;" alt="loading"/>');
        $.getJSON('./validateSchedule', { "schedule" : JSON.stringify(cse.schedule) }, function(data) {
            $('#validationDiv').html('');
            if(data.status === 'noerror') {
                cse.ui.addAlertDiv(cse.ui.alertValues.SUCCESS, 'There are no apparent ' +
                        'problems with your schedule!  However, this application is not intended ' + 
                        'as a degree auditing tool.  Degree requirements are <i>not</i> validated, ' + 
                        'only scheduling and prerequisites.  Visit with an academic advisor to be sure that ' + 
                        'your schedule meets all degree requirements.', $('#validationDiv'));
            } else {
                $.each(data.warningMessages, function(key, val) {
                    var warnDiv = $('<div/>')
                      .addClass('alert')
                      .html('<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                            '<span class="label label-warning">Warning</span> ' + val);
                    $('#validationDiv').append(warnDiv);
                    warnDiv.hide().show('fade', {}, 1000);
                });
                $.each(data.errorMessages, function(key, val) {
                    var errorDiv = $('<div/>')
                      .addClass('alert')
                      .addClass('alert-error')
                      .html('<button type="button" class="close" data-dismiss="alert">&times;</button>' +
                            '<span class="label label-important">Error</span> ' + val);
                    $('#validationDiv').append(errorDiv);
                    errorDiv.hide().show('fade', {}, 1000);
                });
            }
        }).fail(function() {
            cse.ui.addAlertDiv(cse.ui.alertValues.ERROR, "unable to reach the server", $('#validationDiv'));
        });
    },
    initialize : function() {
        console.log('initializing...');
        //load and handle course data
        $.getJSON('getCourses', function(data) {
            cse.setCourses(data);
        }).fail(function() {
            cse.ui.addAlertDiv(cse.ui.alertValues.ERROR, "An unexpected error occurred, course menu could not be loaded.", $('#courseMenu'));
            cse.ui.addAlertDiv(cse.ui.alertValues.ERROR, "Could not load schedule due to error loading course menu", $('#scheduleDiv'));
        }).success(function() {
            //initialize the menu after the data model has been built...
            cse.ui.initializeCourseMenu();
            //only initialize the schedule *after* the course menu has been built:
            cse.initializeSchedule();
        });
    },
    initializeSchedule: function() {
        console.log("initializing schedule");
        var params = cse.getParameters();
        if(params.buildSchedule) {
            //if the parameters tell us to build a schedule, we do it
            console.log('building schedule...');
            cse.buildSchedule();
        } else if(params.loadRemote) {
            //otherwise if they tell us to load from the database we do it
            console.log('loading remote schedule...');
            cse.loadRemote();
        } else if(params.loadLocal || 
                  localStorage.getItem('schedule')) {
            //otherwise, if they tell us to load locally OR if they have a previously saved schedule in local storage, load it
            console.log('loading local schedule...');
            cse.loadLocal();
        } else {
            //if all else fails, load a new empty schedule
            console.log('loading new empty schedule...');
            cse.loadNewEmptySchedule();
        }        
    },
    menuAddCourse : function() {
        var q = $('#customCourseSearch').val();
        $('#customCourseSearch').val('');
        var courseSubject = null;
        var courseNumber  = null;
        try {
            courseSubject = q.substring(0,4).toUpperCase();
            courseNumber  = q.substring(5, (q.indexOf(':') == -1 ? q.length : q.indexOf(':')));
        } catch(e) {
            console.log("DEBUG: error parsing course search "+e.getMessage());
            return;
        }
        cse.addBulletinCourse(courseSubject, courseNumber);
    },
    addBulletinCourse : function(courseSubject, courseNumber, termCode) {
        if(cse.getCourseById(courseSubject+courseNumber) != null) {
            var errorMessage = $("<div/>")
                .addClass("alert")
                .html('<strong>Warning</strong> Course is already available!')
                .css('margin-top', '0.5em')
                .hide();
            $('#customCourseList')
              .append(errorMessage);
            errorMessage
                .show('bounce', {}, 1500)
                .fadeOut(1000, function() {this.remove();});
            return;
        }
        $.ajax({
            type: "GET",
            url: "https://bulletin.unl.edu/undergraduate/courses/" + courseSubject + "/" + courseNumber + "?format=xml",
            dataType: "xml",
            success: function(xml) {
                var course = {
                    subject : $(xml).find('courseCode[type="home listing"] subject').text(),
                    number  : $(xml).find('courseCode[type="home listing"] courseNumber').text() + $(xml).find('courseCode[type="home listing"] courseLetter').text(),
                    title   : $(xml).find('title').text(),
                    aces     : [],
                    prerequisiteText : $(xml).find('prerequisite div').text(),
                    description: $(xml).find('description div').text(),
                    creditHours : parseInt($(xml).find('credits credit[type="Single Value"]').text())
                };
                if(isNaN(course.creditHours)) {
                    course.creditHours = 0;
                }
                course.courseId = course.subject + course.number;
                var aceElems = $(xml).find('aceOutcomes slo');
                for(var i=0; i<aceElems.length; i++) {
                    course.aces.push($(aceElems[i]).text());
                }
                cse.addCourse(course);
                cse.ui.addCourse(course.courseId);
                if(termCode) {
                    console.log("okay, adding it to the schedule too...");
                    cse.ui.scheduleCourse(course.courseId, termCode);
                }
            },
            error: function() {
                console.log("Unable to get course details");
                var errorMessage = $("<div/>")
                    .addClass("alert")
                    .addClass("alert-error")
                    .html('<strong>Error!</strong> unable to fetch course details.')
                    .css('margin-top', '0.5em')
                    .hide();
                $('#customCourseList')
                  .append(errorMessage);
                errorMessage.show('bounce', {}, 1500);
                errorMessage.fadeOut(1000, function() {this.remove();});
            }
        });        
        return false;
    }
};


jQuery(document).ready(function() {
    cse.initialize();

    $('#newTermYear').spinner();
    
    $('#customCourseSearch').keypress(function(event){
        var e = event.keyCode;
        keyMatch = false;
        if(e == 27 || e == 9 || e == 13){
            keyMatch = true;
            console.log(keyMatch);
        }
    });
    
    $('#studentSearch').autocomplete({
        minLength: 3,
        source: function( request, response ) {
            $.ajax({
                url: "./secure/admin/searchStudent",
                dataType: "jsonp",
                data: {
                  term: request.term,
                  limit: 10
                },
                success: function( data ) {
                  console.log("success...");
                  response( $.map( data, function( item ) {
                      return {
                        label: item.label,
                        value: item.value
                      };
                    }));
                },
                error: function() {
                    console.log('unable to get info...');
                }
            });
        },
        select: function( event, ui ) {
          $( "#studentSearch" ).val( ui.item.label );
          $( "#userLogin" ).val( ui.item.value );
        }
    });
    
    $('#customCourseSearch').autocomplete({
        delay: 555,
        minLength: 2,
        search: function(event, ui){
            if (keyMatch) {
                console.log('invalid key');
                return false;
            }
        },
        source: function(request, response) {
            $.ajax({
                url: 'https://bulletin.unl.edu/undergraduate/courses/search?q='+request.term+'&format=json&limit=10',
                dataType: "json",
                success: function(data) {
                    var rows = [];
                    for(var i=0; i<data.length; i++){
                      //label is for the suggestion
                      //value is for the input box
                            //key is used to match highlighted course
                            rows[i] = { 
                                label: data[i].courseCodes[0].subject + ' ' +
                                       data[i].courseCodes[0].courseNumber + ': ' +
                                       data[i].title +
                                            '<span class="key" style="display:none;">' + data[i].courseCodes[0].subject + data[i].courseCodes[0].courseNumber + data[i].title + '</span>',
                                courseInfo : {
                                    subject: data[i].courseCodes[0].subject,
                                    number: data[i].courseCodes[0].courseNumber,
                                    title: data[i].title
                                },
                                value: data[i].courseCodes[0].subject + " " + data[i].courseCodes[0].courseNumber + ": " + data[i].title,
                                key: data[i].courseCodes[0].subject + data[i].courseCodes[0].courseNumber + data[i].title
                            };
                        }
                    response(rows);
                }
            });
        },
        focus: function(e, ui) {
            $('a.indicator').removeClass('indicator');
            $('a:contains("'+ui.item.key+'")').addClass('indicator');
        },
        select: function(e, ui) {
        }
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
          return $( "<li>" )
              .append('<a><span class="label label-info" style="text-align: center">'+item.courseInfo.subject + "<br>" + item.courseInfo.number+'</span> ' +
                      '<span style="vertical-align: 25%;">'+item.courseInfo.title+'</span></a>')
            .appendTo( ul );
        };
});

