Feature: Autenticar Utilizador
	Como utilizador
	Quero autenticar-me
	Para que possa aceder a funcionalidades extra
	
Scenario: Autenticar User com Email Inválido
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "LOGIN"
	And Preencho "IstoÉUmEmailInválido:)" no campo "Email LOGIN"
	And Preencho "123456789" no campo "Password LOGIN"
	And Clico no botao "LOGIN"
	And Vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Autenticar User com Password Inválida
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "LOGIN"
	And Preencho "testaccount@mail.com" no campo "Email LOGIN"
	And Preencho "passwordinválida:(" no campo "Password LOGIN"
	And Clico no botao "LOGIN"
	And Vejo o text view "Something went wrong The password is invalid or the user does not have a password." 
	Then Fecho a aplicacao

Scenario: Autenticar User Preencher e Cancelar
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "LOGIN"
	And Preencho "testaccount@mail.com" no campo "Email LOGIN"
	And Preencho "123456789" no campo "Password LOGIN"
	And Clico no botao "CANCEL"
	And Vejo o botao "PROFILE"
	Then Fecho a aplicacao

Scenario: Autenticar User
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "LOGIN"
	And Preencho "testaccount@mail.com" no campo "Email LOGIN"
	And Preencho "123456789" no campo "Password LOGIN"
	And Clico no botao "LOGIN"
	And Faço logout
	Then Fecho a aplicacao

Scenario: Autenticar User Continua Autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "LOGIN"
	And Preencho "testaccount@mail.com" no campo "Email LOGIN"
	And Preencho "123456789" no campo "Password LOGIN"
	And Clico no botao "LOGIN"
	And Vejo o botao "LOGOUT"
	And Fecho a aplicacao
	And Eu inicio a aplicacao
	And Faço logout
	Then Fecho a aplicacao

