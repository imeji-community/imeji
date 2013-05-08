/**
 * @author Marco Schlender
 */

function importantDates() {
	this.year = null;
	
	this.roseMondayDiffDays = -48;
	this.fastnachtDiffDays = -47;
	this.aschermittwochDiffDays = -46;
	this.easterSaturdayDiffDays = -1;
	this.easterSunday = null;
	this.easterMondayDiffDays = 1;
	this.karfreitagDiffDays = -2;
	this.greenThursdayDiffDays = -3;
	this.motherdayDiffDays = 14;
	this.christiHimmelfahrtDiffDays = 39;
	this.pfingstSundayDiffDays = 49;
	this.pfingstMondayDiffDays = 50;
	this.fronleichnahmDiffDays = 60;
	this.sevenSleeperDiffDays = 64;
	
	this.importantDates = ['newyear', 'holy3kings', 'rosemonday', 'fastnacht', 'aschermittwoch', 'startsummertime', 'greenthursday', 'karfreyday', 'easterSunday', 'eastermonday', 'motherday', 'walpurgis', 'firstmay',
				'christihimmelfahrt', 'childrensday', 'pfingstsunday', 'pfingstmonday', 'fronleichnahm', 'sevensleeper', 'dayOfGermanUnit', 'thanksgiving', 'endsummertime', 'reformationday', 'allerheiligen', 
				'busbetday', 'deathsunday', 'firstAdvent', 'secondAdvent', 'thirdAdvent', 'fourthAdvent', 'holynight', 'xmas_1_day', 'xmas_2_day', 'silvester' ];
	
	this.calcEasterSunday = function(year) {
	//	year = String(year);
		if (year < 1584) {
			return false;
		} else if (this.year != Number(year)){
			this.year = eval(year);
		}
		
		//1
		var a = this.year%19;	//dividiere with 19 - rest is important
		//2
		var b = Math.floor(this.year/100) //dividiere with 100 and save the quotient
		var c = this.year%100;			//dividiere with 100 - rest is important
		//3
		var d = Math.floor(b/4);
		var e = b%4;
		//4
		var f = Math.floor((b+8)/25);
		//5
		var g = Math.floor((b-f+1)/3);
		//6
		var h = (19*a+b-d-g+15)%30;
		//7
		var i = Math.floor(c/4);
		var j = c%4;
		//8
		var k = (32+2*e+2*i-h-j)%7;
		//9
		var l = Math.floor((a+11*h+22*k)/451);
		//10
		var month = Math.floor((h+j-7*l+114)/31);
		var monthDay = ((h+k-7*l+114)%31) + 1;
		
		//alert(year+'-'+month+'-'+monthDay);
		this.easterSunday = new Date(this.year, month-1, monthDay, 12, 0, 0);
	};
	
	this.calcThanksgiving = function () {	//the first sunday in october
		for (i = 1; i < 8; i++) {
			var tmpDate = new Date(this.year, 9, i, 12, 0, 0);
			if (tmpDate.getDay() == 0) {
				return tmpDate;
				break;
			}
		}
	}
	
	this.calcMETSummertime = function (direction) {
		var tmpDate = null;
		switch (direction) {
			case 'start':
			case 'begin':
				for (i = 31; i > 23; i--) {
					tmpDate = new Date(this.year, 2, i, 3, 0, 0);
					if (tmpDate.getDay() == 0) {
						return tmpDate;
						break;
					}
				}
				break;
			case 'end':
			case 'stop':
				for (i = 30; i > 22; i--) {
					tmpDate = new Date(this.year, 9, i, 3, 0, 0);
					if (tmpDate.getDay() == 0) {
						return tmpDate;
						break;
					}
				}
				break;
		}
	}
	
	this.calcFourthAdvent = function() {
		var tmpDate = new Date(this.year, 11, 25, 12, 0, 0);
		tmpDate.setTime(tmpDate.getTime() - ((tmpDate.getDay() == 0) ? 7 : tmpDate.getDay()) * this.getOneDay('milliseconds'));
		return tmpDate;
	}
	
	this.calcDate = function(day, year) {
		if (this.year != Number(year)) {
			this.calcEasterSunday(year);
		}
		
		switch(day.toLowerCase()) {
			case 'rosemonday':
				return this.getNextDate(this.roseMondayDiffDays, this.easterSunday);
				break;
			case 'fastnacht':
				return this.getNextDate(this.fastnachtDiffDays, this.easterSunday);
				break;
			case 'aschermittwoch':
				return this.getNextDate(this.aschermittwochDiffDays, this.easterSunday);
				break;
			case 'greenthursday':
				return this.getNextDate(this.greenThursdayDiffDays, this.easterSunday);
				break;
			case 'karfreyday':
				return this.getNextDate(this.karfreitagDiffDays, this.easterSunday);
				break;
			case 'karsaturday':
			case 'eastersaturday':
				return this.getNextDate(this.easterSaturdayDiffDays, this.easterSunday);
				break;
			case 'eastersunday':
				return this.easterSunday;
				break;
			case 'eastermonday':
				return this.getNextDate(this.easterMondayDiffDays, this.easterSunday);
				break;
			case 'motherday':
				return this.getNextDate(this.motherdayDiffDays, this.easterSunday);
				break;
			case 'christihimmelfahrt':
				return this.getNextDate(this.christiHimmelfahrtDiffDays, this.easterSunday);
				break;
			case 'pfingstsunday':
				return this.getNextDate(this.pfingstSundayDiffDays, this.easterSunday);
				break;
			case 'pfingstmonday':
				return this.getNextDate(this.pfingstMondayDiffDays, this.easterSunday);
				break;
			case 'fronleichnahm':
				return this.getNextDate(this.fronleichnahmDiffDays, this.easterSunday);
				break;
			case 'sevensleeper':
				return this.getNextDate(this.sevenSleeperDiffDays, this.easterSunday);
				break;
			case 'dayofgermanunit':
				return new Date(this.year, 9, 3);
				break;
			case 'holynight':
				return new Date(this.year, 11, 24);
				break;
			case 'xmas_1_day':
				return new Date(this.year, 11, 25);
				break;
			case 'xmas_2_day':
				return new Date(this.year, 11, 26);
				break;
			case 'silvester':
				return new Date(this.year, 11, 31);
				break;
			case 'newyear':
				return new Date(this.year, 0, 1);
				break;
			case 'holy3kings':
				return new Date(this.year, 0, 6);
				break;
			case 'walpurgis':
				return new Date(this.year, 3, 30);
				break;
			case 'firstmay':
				return new Date(this.year, 4, 1);
				break;
			case 'childrensday':
				return new Date(this.year, 5, 1);
				break;
			case 'thanksgiving':
				return this.calcThanksgiving();
				break;
			case 'reformationday':
				return new Date(this.year, 9, 31);
				break;
			case 'allerheiligen':
				return new Date(this.year, 10, 1);
				break;
			case 'startsummertime':
				return this.calcMETSummertime('start');
				break;
			case 'endsummertime':
				return this.calcMETSummertime('end');
				break;
			case 'busbetday':
				return this.getNextDate(-((7*4)+4), this.calcFourthAdvent());
				break;
			case 'deathsunday':
				return this.getNextDate(-(7*4), this.calcFourthAdvent());
				break;
			case 'nikolaus':
				return new Date(this.year, 11, 6);
				break;
			case 'firstadvent':
				return this.getNextDate(-(7*3), this.calcFourthAdvent());
				break;
			case 'secondadvent':
				return this.getNextDate(-(7*2), this.calcFourthAdvent());
				break;
			case 'thirdadvent':
				return this.getNextDate(-(7), this.calcFourthAdvent());
				break;
			case 'fourthadvent':
				return this.calcFourthAdvent();
				break;
			
			default: 
				return ('not supported date');
		}
	}
	
	this.getNextDate = function (dayDiff, source_date) {
		return (new Date(source_date.getTime() + (dayDiff * this.getOneDay('milliseconds'))));
	}
	
	this.getOneDay = function (format) {
		switch (format.toLowerCase()) {
			case 'minutes':
				return 60 * 24; /* like 60 minutes * 24 hours; */
				break;
			case 'seconds':
				return 60 * 60 * 24; /* like 60 seconds * 60 minutes * 24 hours; */
				break;
			case 'milli':
			case 'milliseconds':
				return 1000 * 60 * 60 * 24; /* like 1000 Milliseconds * 60 seconds * 60 minutes * 24 hours; */
				break;
		}
		return 
	}
	
	this.checkToday = function(checkDate) {
		if (checkDate) {
			var today = new Date(checkDate);
		//	var today = new Date((checkDate.getYear()+1900), checkDate.getMonth(), checkDate.getDate());
		} else {
			var today = new Date();
		}
		
		
		for (ct = 0; ct < this.importantDates.length; ct++) {
			checkDate = this.calcDate(this.importantDates[ct], 2011);
			if (today.getMonth()+''+today.getDate() == checkDate.getMonth()+''+checkDate.getDate()) {
				return this.importantDates[ct];
				break;
			}
		}
	}
};

importantDate = new importantDates;
/*
var wishYear = 2012;

var output = '';

output += 'Feiertage '+wishYear;
output += '\nNeujahr: '+importantDate.calcDate('newyear', wishYear);
output += '\nHeilige 3 Koenige: '+importantDate.calcDate('holy3kings', wishYear);
output += '\nRosenmontag: '+importantDate.calcDate('rosemonday', wishYear);
output += '\nFastnacht: '+importantDate.calcDate('fastnacht', wishYear);
output += '\nAschermittwoch: '+importantDate.calcDate('aschermittwoch', wishYear);
output += '\nSommerzeitumstellung: '+importantDate.calcDate('startsummertime', wishYear);
output += '\nGruendonnerstag: '+importantDate.calcDate('greenthursday', wishYear);
output += '\nKarfreitag: '+importantDate.calcDate('karfreyday', wishYear);
output += '\nKarsamstag: '+importantDate.calcDate('eastersaturday', wishYear);
output += '\nOstersonntag: '+importantDate.calcDate('eastersunday', wishYear);
output += '\nOstermontag: '+importantDate.calcDate('eastermonday', wishYear);
output += '\nMuttertag: '+importantDate.calcDate('motherday', wishYear);
output += '\nWalpurgis: '+importantDate.calcDate('walpurgis', wishYear);
output += '\n1. Mai: '+importantDate.calcDate('firstmay', wishYear);
output += '\nChristi Himmelfahrt: '+importantDate.calcDate('christihimmelfahrt', wishYear);
output += '\nKindertag: '+importantDate.calcDate('childrensday', wishYear);
output += '\nPfingstsonntag: '+importantDate.calcDate('pfingstsunday', wishYear);
output += '\nPfingstmontag: '+importantDate.calcDate('pfingstmonday', wishYear);
output += '\nFronleichnahm: '+importantDate.calcDate('fronleichnahm', wishYear);
output += '\nSiebenschlaefer: '+importantDate.calcDate('sevensleeper', wishYear);
output += '\nTag der deutschen Einheit: '+importantDate.calcDate('dayOfGermanUnit', wishYear);
output += '\nErntedankfest: '+importantDate.calcDate('thanksgiving', wishYear);
output += '\nWinterzeitumstellung: '+importantDate.calcDate('endsummertime', wishYear);
output += '\nReformationstag/Halloween: '+importantDate.calcDate('reformationday', wishYear);
output += '\nAllerheiligen: '+importantDate.calcDate('allerheiligen', wishYear);
output += '\nBuss und Bettag: '+importantDate.calcDate('busbetday', wishYear);
output += '\nTotensonntag: '+importantDate.calcDate('deathsunday', wishYear);
output += '\n1. Advent: '+importantDate.calcDate('firstAdvent', wishYear);
output += '\n2. Advent: '+importantDate.calcDate('secondAdvent', wishYear);
output += '\n3. Advent: '+importantDate.calcDate('thirdAdvent', wishYear);
output += '\n4. Advent: '+importantDate.calcDate('fourthAdvent', wishYear);
output += '\nHeiligabend: '+importantDate.calcDate('holynight', wishYear);
output += '\n1. Weihnachtsfeiertag: '+importantDate.calcDate('xmas_1_day', wishYear);
output += '\n2. Weihnachtsfeiertag: '+importantDate.calcDate('xmas_2_day', wishYear);
output += '\nSilvester: '+importantDate.calcDate('silvester', wishYear);


alert(output);
*/