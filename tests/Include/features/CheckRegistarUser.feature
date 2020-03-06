Feature: Registar Utilizador
	Como utilizador
	Quero registar-me
	Para que possa ter credenciais para fazer login

Scenario: Registar User Mail Invalido
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Preencho "semArroba" no campo "Name"
	And Preencho "123123123" no campo "Password"
	And Preencho "123123123" no campo "Confirm Password"
	And Preencho "abc" no campo "Email"
	And Clico no botao "Register"
	And Vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Registar User Nome Invalido
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Preencho "213#sa%$$d12" no campo "Name"
	And Preencho "123123123" no campo "Password"
	And Preencho "123123123" no campo "Confirm Password"
	And Preencho "abc@gmail.com" no campo "Email"
	And Clico no botao "Register"
	And Vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Registar User Cancelar Profile
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Clico no botao "CANCEL"
	And Nao vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Registar User Mail Vazio
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Preencho "semArroba" no campo "Name"
	And Preencho "123123123" no campo "Password"
	And Preencho "123123123" no campo "Confirm Password"
	And Preencho "" no campo "Email"
	And Clico no botao "Register"
	And Vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Registar User Password Caracteres Insufecientes
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Preencho "dasd asad" no campo "Name"
	And Preencho "123" no campo "Password"
	And Preencho "123" no campo "Confirm Password"
	And Preencho "testaccount@mail.com" no campo "Email"
	And Clico no botao "Register"
	And Vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Registar User Mail Existente
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Preencho "usera adasd" no campo "Name"
	And Preencho "123123123" no campo "Password"
	And Preencho "123123123" no campo "Confirm Password"
	And Preencho "testaccount@mail.com" no campo "Email"
	And Clico no botao "Register"
	And Vejo o text view "Something went wrong The email address is already in use by another account." 
	Then Fecho a aplicacao

Scenario: Registar User Password Verificada Incoerente
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Preencho "sem Arroba" no campo "Name"
	And Preencho "123123123" no campo "Password"
	And Preencho "124124124" no campo "Confirm Password"
	And Preencho "testaccount@mail.com" no campo "Email"
	And Clico no botao "Register"
	And Vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Registar User Nome Vazio
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Preencho "" no campo "Name"
	And Preencho "123123123" no campo "Password"
	And Preencho "123123123" no campo "Confirm Password"
	And Preencho "abcasd@gmail.com" no campo "Email"
	And Clico no botao "Register"
	And Vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Registar User Password Vazia
	Given Eu inicio a aplicacao 
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER"
	And Preencho "dasda" no campo "Name"
	And Preencho "" no campo "Password"
	And Preencho "" no campo "Confirm Password"
	And Preencho "abcasd@gmail.com" no campo "Email"
	And Clico no botao "Register"
	And Vejo o botao "CANCEL"
	Then Fecho a aplicacao

Scenario: Registar User Profile
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao se existir "LOGOUT" 
	And Clico no botao "REGISTER" 
	And Preencho "user antonio" no campo "Name"
	And Preencho "123123123" no campo "Password"
	And Preencho "123123123" no campo "Confirm Password"
	And Preencho "emailnaoexistente@mail.pt" no campo "Email"
	And Clico no botao "Register"
	And Vejo o botao "LOGOUT"
	And Clico no botao "DELETE MY ACCOUNT" 
	And Clico no botao "YES" 
	And Preencho "123123123" no campo "Insert Password" 
	And Clico no botao "DELETE" 
	And Vejo o text view "Account deleted" 
	Then Fecho a aplicacao
