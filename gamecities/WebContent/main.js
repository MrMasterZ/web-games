// инициализируем переменные

// Регулярные выражения
// регулярное выражение (для проверки правильности ввода города пользователем)
// принимаются только слова, начинающиейся с русской буквы и состоящие только из русских букв (регистр не важен) и знаков дефис (-)
var cityPattern =  /^[а-я]+([-]?[а-я]+)*$/i;
var request;                               

var cities = [];                            // массив куда записываются уже введённые города

// сделать неактивными элементы ввода, в которые пользователь не должен вводить значения (input и textarea)
document.getElementById('city-robot').setAttribute('disabled', 'true');
document.getElementById('list-cities').setAttribute('disabled', 'true');

// прячем вывод ошибок
hideError();                

// строка ввода города в фокусе
 $('#city-user').bind('focus', function city1(event) {
   var city = document.getElementById('city-user');
   if (city.value === 'введите название города') { $('#city-user').removeAttr('value'); }
     city.focus();
   });

// строка ввода города без фокуса
$('#city-user').bind('blur', function city2(event) {
  var city = document.getElementById('city-user');
  if (city.value === '') { $('#city-user').attr('value', 'введите название города'); }
  city.blur()
});

// прятать вывод ошибок, чтобы не было накладок сообщений
function hideError() {
  $('#error-format-city').hide();
  $('#error-repetition-city').hide();
  $('#error-rule-city').hide();
  $('#error-exist-city').hide(); 
}

// проверка уникальности введенного пользователем слова (нет ли ещё такого слова в textarea)
function checkUniqueness(cityUserName) {
  for (var i = 0; i < cities.length; i++) {
    if (cities[i] === cityUserName) { return "false"; }      // если повтор слова (если пользователь ввёл слово, которое уже есть в textarea)
  }
  return "true";
}

// заполнение textarea
function fillingTextarea(cityUserName) {
  cities.push(cityUserName);                                 // вводим в массив слово, введённое пользователем
  $('#list-cities').val(cities);                               // пишем массив в textarea
}

// логика кнопки "Ответить"
$(document).ready(function(){
  $('#answer').unbind('click');
  
  $('#answer').click(function(){
    var cityUserName = document.getElementById('city-user').value;                          // читать значение из поля для ввода города
	cityUserName = cityUserName[0].toUpperCase() + cityUserName.substring(1).toLowerCase();  // форматируем введённое слово (первая буква заглавная, остальные - строчные)
	var notUseCity = "true";
	
    if ($('#city-user').val() != 'введите название города') {                         // выполнять только в случае если пользователь ввёл какое-то слово
	  hideError();                                                                    // при нажатии сразу прячем все сообщения об ошибках (чтобы не было накладок сообщений)
      if (!cityPattern.test(cityUserName)) { $('#error-format-city').show(); }        // если введенное слово не соотвествует регулярному выражению, то вывести сообщение об ошибке
	  else {                                         
	    if (cities.length !== 0) {                                                    // проверять только если в textarea уже что-то есть
		   notUseCity = checkUniqueness(cityUserName);                               // проверка на существование слова в textarea (для избежния повторов)
	    }
		
	    if (notUseCity === "true" ) {                                                  // если такое слово ещё не было введено
		if (document.getElementById('city-robot').value !== '') {                      // если это не первый ход пользователя, т.е. компьютер уже ввел свой ответ, то проверяем ввод пользователя на соответствие правилам игры
		var cityRobotName = document.getElementById('city-robot').value;
	    var llr = cityRobotName[cityRobotName.length-1];                              // последняя буква в имени города компьютера (llr = lastLetterRobot)
		var flu = cityUserName[0].toLowerCase();                                      // первая буква в имени города пользователя  (flu = firstLetterUser)
		
		// проверяем соответствует ли введенное пользователем слово правилам игры.
		var eFlag = "0";                                                              // флаг ошибки (если равен 1, то ошибка)
		if (llr==="ё" && flu!="ё" && flu!="е") eFlag = "1";
		if (llr==="й" && flu!="и" && flu!="й") eFlag = "1";
		if ((llr==="ъ" || llr==="ы" || llr==="ь") && flu!=cityRobotName[cityRobotName.length-2]) eFlag = "1";
		if (llr!="ё" && llr!="й" && llr!="ъ" && llr!="ы" && llr!="ь" && flu!=llr) eFlag = "1";
		if (eFlag==="1") $('#error-rule-city').show();
		else workAnswerButton(cityUserName);
		}
		else workAnswerButton(cityUserName);
		}
		else {
		  $('#error-repetition-city').show();                                         // показать сообщение о повторе города
		}
	  }
    }
  });
});

// основная работа кнопки "Ответить"
function workAnswerButton(cityUserName) {
//  fillingTextarea(cityUserName);                                                   // заполнить textarea
  document.getElementById("log").innerHTML = "Консоль выполнения запроса:";
  document.getElementById('city-user').setAttribute('disabled', 'true');
  if (document.getElementById('city-robot').value === '') {
    startAjax();                                                                   // создать объект XMLHttpRequest (если это первый запрос)
  }
  continueAjax('http://localhost:8080/gamecities/gamecities', cityUserName);      // отправить имя города, введённое пользователем серверу и получить ответ
  document.getElementById('city-user').removeAttribute('disabled', 'true');
}

// создаём объект XMLHttpRequest
function startAjax(){

  if(window.XMLHttpRequest){
    request = new XMLHttpRequest();
  } else if(window.ActiveXObject){
    request = new ActiveXObject("Microsoft.XMLHTTP");  
  } else {
      alert("Этот браузер не подходит для этой игры");
	  return;
  }
}
  
// ajax POST-запрос серверу
function continueAjax(url, cityUserName){
// при изменении статуса состояния запроса выводить в лог сообщения и по завершении (если всё хорошо, то код возврата HTTP 200), обработать данные полученные от сервера 
  request.onreadystatechange = function(){
    switch (request.readyState) {
      case 1: printConsole("<br/><em>1: Подождите, идёт подготовка к отправке Вашего слова серверу</em>"); break
      case 2: printConsole("<br/><em>2: Подождите, Ваше слово отправлено серверу и оно обрабатывается</em>"); break
      case 3: printConsole("<br/><em>3: Подождите, идёт обмен данными с сервером</em>"); break
      case 4:{
        if(request.status==200){    
          printConsole("<br/><em>4: Обмен данными завершен, можете вводить следующее слово</em>");
		  var req = request.responseText.replace(/\r?\n/g, "");
		  processingResponseRobot(req, cityUserName);                              // обработка ответа, полученного от компьютера
        }else if(request.status==404){
          alert("Ошибка: запрашиваемый скрипт не найден!");
        }
        else alert("Ошибка: сервер вернул статус: "+ request.status);
          break;
      }
    }      
  }
  
// отправить асинхронный POST-запрос серверу, передав имя города введенное пользователем  
  request.open("POST",url, true);
  request.setRequestHeader("Content-type","application/x-www-form-urlencoded");
  var requestSend = "userCity="+encodeURIComponent(cityUserName);
  request.send(requestSend);
}

// функция вывода в лог
function printConsole(text){
  document.getElementById("log").innerHTML += text;
}

// возврат элементов в первоначальное состояние
function returnToStart(){
  hideError(); 
  $('#city-user').val('введите название города');
  $('#city-robot').val('');
  cities = [];
  $('#list-cities').val(cities);
  document.getElementById("log").innerHTML = "Консоль выполнения запроса:";
  continueAjax('http://localhost:8080/gamecities/gamecities', "clear");
}

// обработка ответа компьютера
function processingResponseRobot(response, cityUserName) {
  switch (response) {
    case "errorNotExists": $('#error-exist-city').show(); break
	case "errorGiveUp": {
	  alert("Поздравляю!!! Вы выиграли!!!");
      returnToStart();	
	  break }
	case "errorBd": {
	  alert("Извините, временные проблемы с БД. Пожалуйста, перезайдите в игру позже.");
      returnToStart();	
	  break }
	case "errorOther": {
	  alert("Извините, произошёл сбой. Нажмите кнопку Ответить снова");
	  break }
	case "clear": {
	  document.getElementById("log").innerHTML = "Консоль выполнения запроса:";
	  break }
    default: {  
      $('#city-robot').val(response);
	  fillingTextarea(cityUserName);
      fillingTextarea(response);
	  break
    }
  }
 }
  
// логика кнопки "Сдаться"
$(document).ready(function(){
  $('#give-up').unbind('click');
    $('#give-up').click(function(){
      alert("К сожалению, Вы проиграли.");
      returnToStart();
    });
});