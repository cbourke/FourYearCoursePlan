<%@ page language="java" import="unl.cse.entities.User" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
  User u = (User) session.getAttribute("user");
  if(u == null || !u.isAdmin()) {
	session.invalidate();
	response.sendRedirect(request.getContextPath() + "/err/403.html");
  }
%>
<!DOCTYPE html>
<!--[if IEMobile 7 ]><html class="ie iem7"><![endif]-->
<!--[if lt IE 7 ]><html class="ie ie6" lang="en"><![endif]-->
<!--[if IE 7 ]><html class="ie ie7" lang="en"><![endif]-->
<!--[if IE 8 ]><html class="ie ie8" lang="en"><![endif]-->
<!--[if (gte IE 9)|(gt IEMobile 7) ]><html class="ie" lang="en"><![endif]-->
<!--[if !(IEMobile) | !(IE)]><!--><html lang="en"><!-- InstanceBegin template="/Templates/fixed.dwt" codeOutsideHTMLIsLocked="false" --><!--<![endif]-->
<head>
<base href="../../"/>
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
<meta name="viewport" content="initial-scale=1.0, width=device-width" />

<!-- For Microsoft -->
<!--[if IE]>
<meta http-equiv="cleartype" content="on">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<![endif]-->

<!-- For iPhone 4 -->
<link rel="apple-touch-icon-precomposed" sizes="114x114" href="/wdn/templates_3.1/images/h-apple-touch-icon-precomposed.png">
<!-- For iPad 1-->
<link rel="apple-touch-icon-precomposed" sizes="72x72" href="/wdn/templates_3.1/images/m-apple-touch-icon-precomposed.png">
<!-- For iPhone 3G, iPod Touch and Android -->
<link rel="apple-touch-icon-precomposed" href="/wdn/templates_3.1/images/l-apple-touch-icon-precomposed.png">
<!-- For everything else -->
<link rel="shortcut icon" href="/wdn/templates_3.1/images/favicon.ico" />
<!-- Load our base CSS file -->
    <link rel="stylesheet" type="text/css" media="all" href="./wdn/templates_3.1/css/compressed/base.css">
<!-- Then load the various media queries (use 'only' to force non CSS3 compliant browser to ignore) -->
<!-- Since this file is media query imports, IE 7 & 8 will not parse it -->
<!--[if gt IE 8]><!-->
    <link rel="stylesheet" type="text/css" media="all and (min-width: 320px)" href="./wdn/templates_3.1/css/variations/media_queries.css">
<!--<![endif]-->
    
<!-- Load the template JS file -->
<!-- <script type="text/javascript" src="./wdn/templates_3.1/scripts/compressed/all.js?dep=3.1.15" id="wdn_dependents"></script> -->

<!-- For old IE, bring in all the styles w/o media queries -->
<!--[if lt IE 9]>
    <link rel="stylesheet" type="text/css" media="all" href="http://www.unl.edu/wdn/templates_3.1/css/compressed/combined_widths.css" />
    <script src="//html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<!-- For all IE versions, bring in the tweaks -->
<!--[if IE]>
    <link rel="stylesheet" type="text/css" media="all" href="http://www.unl.edu/wdn/templates_3.1/css/variations/ie.css" />
<![endif]-->

    <title>Four Year Course Planner | CSE | UNL</title>
	<base href=""/>
	<link rel="stylesheet" href="assets/jquery-ui-1.10.3/css/smoothness/jquery-ui-1.10.3.custom.min.css" />
	<link rel="stylesheet" href="assets/qtip2-2013.05.22/jquery.qtip.min.css" />
	<link rel="stylesheet" href="css/main.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="assets/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet" media="screen">
    
</head>
<body class="fixed hide-wdn_identity_management hide-wdn_navigation_wrapper hide-wdn_navigation_bar hide-breadcrumbs hide-wdn_search hide-wdn_tool_links" data-version="3.1">
    <div id="wdn_wrapper">
        <header id="header" role="banner">
            <a id="logo" href="http://www.unl.edu/" title="UNL website">UNL</a>
            <span id="wdn_institution_title">University of Nebraska&ndash;Lincoln</span>
            <span id="wdn_site_title">CSE Four Year Course Planner</span>
            <div id="wdn_identity_management" role="navigation" aria-labelledby="wdn_idm_username">
			    <a class="wdn_idm_user_profile" id="wdn_idm_login" href="https://login.unl.edu/cas/login" title="Log in to UNL">
			        <img id="wdn_idm_userpic" src="./wdn/templates_3.1/images/wdn_idm_defaulttopbar.gif" alt="User Profile Photo">
			        <span id="wdn_idm_username" style="vertical-align: -10%;">UNL Login</span>
			    </a>
			    <h3 class="wdn_list_descriptor hidden">Account Links</h3>
			    <ul id="wdn_idm_user_options">
			        <li id="wdn_idm_logout">
			            <a title="Logout" href="https://login.unl.edu/cas/logout?url=http%3A//www.unl.edu/">Logout</a>
			        </li>
			    </ul>
			</div>
<div id="wdn_search">
    <form id="wdn_search_form" action="http://www.google.com/u/UNL1?sa=Google+Search&amp;q=" method="get" role="search">
        <fieldset>
            <legend class="hidden">Search</legend>
            <label for="q">Search this site, all UNL or for a person</label>
            <input accesskey="f" id="q" name="q" type="search" placeholder="Search this site, all UNL or for a person" />
            <input class="search" type="submit" value="Go" name="submit" />
        </fieldset>
    </form>
</div>
<h3 class="wdn_list_descriptor hidden">UNL Tools</h3>
<menu id="wdn_tool_links">
    <li><a href="http://www1.unl.edu/feeds/" class="feeds tooltip" data-title="RSS Feeds: View and Subscribe to News Feeds">Feeds</a></li>
    <li><a href="http://forecast.weather.gov/MapClick.php?CityName=Lincoln&amp;state=NE&amp;site=OAX" class="weather tooltip" data-title="Weather: Local Forecast and Radar">Weather</a></li>
    <li><a href="http://events.unl.edu/" class="events tooltip" data-title="UNL Events: Calendar of Upcoming Events">Events</a></li>
    <li><a href="http://directory.unl.edu/" class="directory tooltip" data-title="UNL Directory: Search for Faculty, Staff, Students and Departments">Directory</a></li>
</menu>
<span class="corner-fix-top-right"></span>
<span class="corner-fix-bottom-left"></span>
        </header>

<!-- begin nav section -->
        <div id="wdn_navigation_bar" role="navigation">
            <nav id="breadcrumbs">
                <!-- WDN: see glossary item 'breadcrumbs' -->
                <h3 class="wdn_list_descriptor hidden">Breadcrumbs</h3>
                <!-- InstanceBeginEditable name="breadcrumbs" -->
                <ul>
                    <li><a href="http://www.unl.edu/" title="University of Nebraska&ndash;Lincoln">UNL</a></li>
                    <li><a href="http://cse.unl.edu/" title="Department of Computer Science &amp; Engineering">CSE</a></li>
 					<li><a href="#">Four Year Course Planner</a></li>
                </ul>
                <!-- InstanceEndEditable -->
            </nav>
            <div id="wdn_navigation_wrapper" style="display: none;">
                <nav id="navigation" role="navigation">
                    <h3 class="wdn_list_descriptor hidden">Navigation</h3>
                    <!-- InstanceBeginEditable name="navlinks" -->
					<ul>
					    <li><a href="#">Start</a>
					        <ul>
					            <li><a href="setup.jsp">Quick Start</a></li>
					            <li><a href="secure/index.jsp">Login</a></li>
					        </ul>
					    </li>
					    <li><a href="#">Resources</a>
					        <ul>
					            <li><a href="http://bulletin.unl.edu/undergraduate/">Undergraduate Bulletin</a></li>
					            <li><a href="http://bulletin.unl.edu/undergraduate/major/Computer+Science">Computer Science Major</a></li>
					            <li><a href="http://bulletin.unl.edu/undergraduate/major/Computer+Engineering+%28Lincoln%29">Computer Engineering Major</a></li>
					        </ul>
					    </li>
					    <li><a href="#">Related</a>
					        <ul>
					            <li><a href="http://cse.unl.edu">CSE Home</a></li>
					            <li><a href="http://cse.unl.edu/advising">CSE Advising Home</a></li>
					        </ul>
					    </li>
					
					</ul>
                    <!-- InstanceEndEditable -->
                </nav>
            </div>
        </div>
<!-- end nav section -->

        <div id="wdn_content_wrapper" role="main">
		<div id="maincontent"></div>
<!-- BEGIN APP CODE -->
<div>

	<h4 id="advisorBanner" style="margin-top: -2em;">Advisor Interface</h4>
	<h6>Currently Working with User: <span class="currentUser">No User Loaded</span></h6>
    <div class="navbar">
      <div class="navbar-inner">
        <div class="container">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <div class="nav-collapse collapse">
            <ul class="nav">
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Schedule <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="#clearModal" role="button" data-toggle="modal">New Schedule</a></li>
                  <li class="divider"></li>
                  <li><a href="#addTermModal" role="button" data-toggle="modal">Add Term(s)</a></li>
                  <!-- voided js targets as an IE fix -->
                  <li><a id="addTransferMenuItem" data-target="#" href="javascript:void(0);" onclick="cse.addTerm(cse.transferTermCode);">Add Transfer Credits</a></li>
                  <li><a id="trimTermsMenuItem" data-target="#" href="javascript:void(0);" onclick="cse.trimTerms();">Clean up</a></li>
                </ul>
              </li>
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Save/Load <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="#saveRemoteModal" role="button" data-toggle="modal">Save</a></li>
                </ul>
              </li>
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Action <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="#" role="button" data-toggle="modal" onclick="cse.getPdf();">Download as PDF</a></li>
                  <li class="divider"></li>
                  <li><a href="#optionsModal" role="button" data-toggle="modal">Options</a></li>
                  <li><a href="#" role="button" data-toggle="modal" onclick="cse.validateSchedule()">Validate Plan</a></li>
                </ul>
              </li>
              <li><a href="#helpModal" data-toggle="modal">Help</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

	<div id="mainContainer" class="container-fluid" style="margin-top: 1em;">
	
	    <div id="mainRow" class="row-fluid">

      <div id="courseMenuContainer" class="span3">
        <h3>Courses</h3>
        <div id="courseMenu">
        	<img src="./img/loading48x48-trans.gif" style="margin-left: 2em;" alt="loading"/>
        </div>
        <div id="courseSearchMenu">
		  <h4 class="ui-widget-header ui-corner-all" style="text-align: center;">Course Search</h4>
		  <div class="formContainer">
			<input type="text" id="customCourseSearch" class="span12 pull-right" placeholder="Search for Course">
		    <button class="btn btn-mini btn-primary pull-right" onclick="cse.menuAddCourse();">Add</button>
		  </div>
		  <div style="clear: both;">&nbsp;</div>
          <div id="customCourseList">
          </div>
        </div><!-- end courseSearchMenu -->
      </div><!--end courseMenuContainer -->

      <div id="scheduleContainer" class="span6">
      	<h3>Schedule</h3>
      	<div id="scheduleDiv" class="span11">
        	<img src="./img/loading48x48-trans.gif" style="margin-left: 2em;" alt="loading"/>
        </div><!-- end scheduleDiv -->
        <div class="creditHours" id="totalCreditHours">Total Credit Hours: 0</div>
      </div><!-- end scheduleContainer -->

      <div id="optionsMenu" class="span3">
      	<h3>Information</h3>
      	<div id="informationDiv">
	      	<div class="alert alert-info">
		      <button type="button" class="close" data-dismiss="alert">&times;</button>
			  <span class="label label-info">Note</span> Welcome to the 4 Year Course Planner!
	        </div>
       </div>

        <div id="advisorStudentSearch">
		  <h3>Advisor Menu</h3>
		  <div class="formContainer">
		    <input type="hidden" id="userLogin"/>
			<input type="text" id="studentSearch" class="span12 pull-right" placeholder="Search for Student">
		    <button class="btn btn-mini btn-primary pull-right" onclick="cse.advisorLoad();">Load Schedule</button>
		  </div>
		  <div style="clear: both;">&nbsp;</div>
        </div><!-- end advisorStudentSearch -->
        
      	<div id="validationContainer">
	      <h3>Validation</h3>
	      <div id="validationDiv">
	      	<div class="alert alert-info">
		      <button type="button" class="close" data-dismiss="alert">&times;</button>
			  <span class="label label-info">Note</span> Validate your academic plan, go to Action &rarr; Validate Plan.
	        </div>
	      </div>
	      
      </div>
    </div><!-- end mainRow -->
	
	
	</div>

</div>
<!-- END OF APP CODE -->	
<div class="clear" style="height: 4em;"></div>

        </div><!-- end wdn_content_wrapper -->
        <footer id="footer">
            <div id="footer_floater"></div>
            <!-- InstanceBeginEditable name="optionalfooter" -->
            <!-- InstanceEndEditable -->
            <div id="wdn_copyright">
                <div>
                    <!-- InstanceBeginEditable name="footercontent" -->
<div id="footercontent" class="region region-footercontent footercontent">
  <div id="block-block-104" class="block block-block">
    <div class="content">
      &copy; 2013 Computer Science &amp; Engineering | University of Nebraska&dash;Lincoln | Lincoln, NE 68588-0115 | 402-472-2401 |<a href="mailto:website@cse.unl.edu" title="Click here to direct your comments and questions">comments?</a><br>
      UNL is an equal opportunity employer with a comprehensive plan for diversity. Find out more: <a href="https://employment.unl.edu/" title="Employment at UNL">employment.unl.edu</a>  
    </div>
  </div>
</div>

                    <!-- InstanceEndEditable -->
<span id="wdn_attribution"><br/>UNL web templates and quality assurance provided by the <a href="http://wdn.unl.edu/" title="UNL Web Developer Network">Web Developer Network</a> | <a href="http://www1.unl.edu/wdn/qa/" id="wdn_qa_link">QA Test</a></span>                </div>
<div id="wdn_logos">
    <a href="http://www.unl.edu/" title="UNL Home" id="unl_wordmark">UNL Home</a>
    <a href="http://www.cic.net/home" title="CIC Website" id="cic_wordmark">CIC Website</a>
    <a href="http://www.bigten.org/" title="Big Ten Website" id="b1g_wordmark">Big Ten Website</a>
</div>            </div>
        </footer>
    </div>
</div>
    <script src="assets/jquery/jquery-1.10.1.min.js"></script>
    <script src="assets/jquery-ui-1.10.3/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="assets/touchPunch/jquery.ui.touch-punch.min.js"></script>
    <script src="assets/bootstrap/js/bootstrap.min.js"></script>
    <script src="assets/qtip2-2013.05.22/jquery.qtip.min.js"></script>
    <script src="js/main.min.js"></script>

    <!-- begin modals for bootstrap -->	
    
    <div id="saveRemoteModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="saveRemoteModalLabel" aria-hidden="true">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="saveModalLabel">Are you sure?</h3>
    </div>
    <div class="modal-body">
      <p>This will save the current schedule to our database for the user <span class="currentUser">No User Selected</span>, overwriting any previously saved schedules.</p>
    </div>
    <div class="modal-footer">
      <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
      <button class="btn btn-primary" data-dismiss="modal" onclick="cse.saveRemote();">Confirm</button>
    </div>
    </div>

    <div id="loadRemoteModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="loadRemoteModalLabel" aria-hidden="true">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="loadModalLabel">Are you sure?</h3>
    </div>
    <div class="modal-body">
      <p>This will load your schedule from our database, you will lose any changes that you've made.</p>
    </div>
    <div class="modal-footer">
      <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
      <button class="btn btn-primary" data-dismiss="modal" onclick="cse.loadRemote();">Confirm</button>
    </div>
    </div>

    <div id="clearModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="clearModalLabel" aria-hidden="true">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="clearModalLabel">Are you sure?</h3>
    </div>
    <div class="modal-body">
      <p>This will clear the entire schedule; you will lose any changes you've made.</p>
      <p>Optionally, you may setup a new schedule starting in a different semester:</p>
		<select id="newScheduleSem">
		  <option value="8">Fall</option>
		  <option value="1">Spring</option>
		</select>
		<input type="text" id="newScheduleYear" value="2013"/>
    </div>
    <div class="modal-footer">
      <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
      <button class="btn btn-primary" data-dismiss="modal" onclick="cse.loadNewEmptySchedule(true);">Confirm</button>
    </div>
    </div>
    
    <div id="addTermModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="addTermModalLabel" aria-hidden="true">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="addTermModalLabel">Add Term(s)</h3>
    </div>
    <div class="modal-body">
      <p>Select a term to add.  All intermediate fall and spring terms will be added as well.</p>
       <select id="newTermSem">
		  <option value="8">Fall</option>
		  <option value="1">Spring</option>
		  <option value="6">Summer</option>
		</select>
		<input type="text" id="newTermYear" value="2013" readonly="readonly" style="background-color: white"/>
    </div>
    <div class="modal-footer">
      <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
      <button class="btn btn-primary" data-dismiss="modal" onclick="cse.ui.addTerm();">Confirm</button>
    </div>
    </div>

    <div id="helpModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="helpModalLabel" aria-hidden="true">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="helpModalLabel">Help</h3>
    </div>
    <div class="modal-body">
      <p>TODO</p>
    </div>
    <div class="modal-footer">
      <button class="btn btn-primary" data-dismiss="modal">Okay</button>
    </div>
    </div>
    
    <div id="optionsModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="optionsModalLabel" aria-hidden="true">
    <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h3 id="optionsModalLabel">Options</h3>
    </div>
    <div class="modal-body">
      <h4>Degree Plan</h4>
	    <label class="radio">
	      <input type="radio" name="degreeType" id="degreeType-CS" value="1"> 
	      Computer Science
	    </label>
	    <label class="radio">
	      <input type="radio" name="degreeType" id="degreeType-CE" value="2">
	      Computer Engineering
	    </label>
      <p>More options to come!</p>
    </div>
    <div class="modal-footer">
      <button class="btn btn-primary" data-dismiss="modal" onclick="cse.updateOptions();">Done!</button>
    </div>
    </div>
    <!-- end modals -->
    
</body>
<!-- InstanceEnd --></html>
