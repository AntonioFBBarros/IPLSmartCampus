Feature: Estatisticas
	Como utilizador
	Quero aceder ás estatísticas
	Para ver representações gráficas das estatísticas relativas a qualidade do ar nos respectivos locais

Scenario: Verifico que vejo estatisticas
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Statistics"
	And Vejo o grafico "Temperature"
	And Dou scroll ate ver o texto "SET HUMIDITY LIMITS"
	And Vejo o grafico "Humidity"
	And Dou scroll ate ver o texto "SET QUALITY LIMITS"
	And Vejo o grafico "Quality"
	And Faço logout
	Then Fecho a aplicacao
	
Scenario: Verifico que consigo ver estatisticas de uma sala
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Statistics"
	And Clico no botao "Locais"
	And Clico no botao "ALL"
	And Seleciono o local "1.B"
	And Vejo o grafico "Temperature"
	And Dou scroll ate ver o texto "SET HUMIDITY LIMITS"
	And Vejo o grafico "Humidity"
	And Dou scroll ate ver o texto "SET QUALITY LIMITS"
	And Vejo o grafico "Quality"
	And Faço logout
	Then Fecho a aplicacao

Scenario: Verifico que não consigo inserir limites inválidos para um gráfico
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Statistics"
	And Preencho "0" no campo "txtTempMin"
	And Clico back
	And Preencho "0" no campo "txtTempMax"
	And Clico no botao "SET TEMPERATURE LIMITS"
	And Vejo o text view "Please insert valid dates (dd-mm-yyyy hhmm)"
	And Clico no botao "OK"
	And Preencho "01-12-2019 00:00" no campo "txtTempMin"
	And Preencho "30-11-2019 00:00" no campo "txtTempMax"
	And Clico no botao "SET TEMPERATURE LIMITS"
	And Vejo o text view "Make sure your minimum date is before your maximum date"
	And Clico no botao "OK"
	And Faço logout
	Then Fecho a aplicacao
	
Scenario: Verifico que consigo inserir limites para um gráfico
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "Statistics"
	And Preencho "28-11-2019 00:00" no campo "txtTempMin"
	And Clico back
	And Preencho "05-12-2019 00:00" no campo "txtTempMax"
	And Clico no botao "SET TEMPERATURE LIMITS"
	And Nao vejo o text view "Please insert valid dates (dd-mm-yyyy hhmm)"
	And Vejo o grafico "Temperature"
	And Faço logout
	Then Fecho a aplicacao
	
Scenario: Verifico que como utilizador não autenticado não consigo aceder às estatisticas
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Nao vejo o botao "Statistics"
	Then Fecho a aplicacao

