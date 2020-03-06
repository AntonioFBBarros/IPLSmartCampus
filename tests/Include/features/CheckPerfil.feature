Feature: Perfil
 Como utilizador autenticado
 Quero poder alterar os meus dados, apagar a minha conta e apagar os dados da minha exposicao
 Para que possa gerir os meus dados pessoais


Scenario: Apagar conta
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "DELETE MY ACCOUNT" 
	And Clico no botao "YES" 
	And Preencho "123456789" no campo "Insert Password" 
	And Clico no botao "DELETE" 
	And Vejo o text view "Account deleted" 
	And Clico back
	And Clico no botao "LOGIN" 
	And Preencho "testaccount@mail.com" no campo "Email" 
	And Preencho "123456789" no campo "Password" 
	And Clico no botao "LOGIN" 
	And Vejo o text view "Something went wrong There is no user record corresponding to this identifier. The user may have been deleted." 
	And Clico back
	And Clico no botao "CANCEL" 
	And Clico no botao "REGISTER" 
	And Preencho "testaccount" no campo "Name" 
	And Preencho "testaccount@mail.com" no campo "Email" 
	And Preencho "123456789" no campo "Password" 
	And Preencho "123456789" no campo "Confirm Password" 
	And Clico no botao "REGISTER" 
	And Vejo o botao "LOGOUT" 
	And Faço logout
	Then Fecho a aplicacao

Scenario: Recuperar password
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Clico no botao "LOGIN"
	And Preencho "testaccount@mail.com" no campo "Email" 
	And Clico no text view "Forgot Password"
	And Vejo o text view "email sent" 
	And Clico back
	And Clico no botao "CANCEL" 
	Then Fecho a aplicacao
	
Scenario: Apagar conta nao autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Nao vejo o botao "DELETE MY ACCOUNT"
	Then Fecho a aplicacao

Scenario: Alterar username nao autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Nao vejo o botao "CHANGE USERNAME"
	Then Fecho a aplicacao
	
Scenario: Alterar password nao autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Nao vejo o botao "CHANGE PASSWORD"
	Then Fecho a aplicacao
	
Scenario: Apagar my exposure nao autenticado
	Given Eu inicio a aplicacao
	When Clico no botao "PROFILE"
	And Nao vejo o botao "DELETE MY EXPOSORE DATA"
	Then Fecho a aplicacao

Scenario: Alterar username
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "CHANGE USERNAME"
	And Preencho "TESTETESTE" no campo "New Name" 
	And Clico no botao "CHANGE" 
	And Vejo o text view "Username updated" 
	And Clico back
	And Clico no botao "BACK" 
	And Vejo o text view "TESTETESTE" 
	And Faço logout
	And Faço login "testaccount@mail.com" "123456789" 
	And Clico no botao "PROFILE" 
	And Clico no botao "CHANGE USERNAME" 
	And Preencho "testeaccount" no campo "New Name" 
	And Clico no botao "CHANGE" 
	And Vejo o text view "Username updated" 
	And Clico back
	And Clico no botao "BACK" 
	And Vejo o text view "testeaccount" 
	And Faço logout
	Then Fecho a aplicacao

Scenario: Alterar password
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "CHANGE PASSWORD" 
	And Preencho "123456789" no campo "Old Password" 
	And Preencho "987654321" no campo "New Password" 
	And Preencho "987654321" no campo "Confirm Change Password" 
	And Clico no botao "CHANGE" 
	And Vejo o text view "Password updated" 
	And Clico back
	And Clico no botao "BACK" 
	And Faço logout
	And Faço login "testaccount@mail.com" "987654321" 
	And Clico no botao "PROFILE" 
	And Clico no botao "CHANGE PASSWORD" 
	And Preencho "987654321" no campo "Old Password" 
	And Preencho "123456789" no campo "New Password" 
	And Preencho "123456789" no campo "Confirm Change Password" 
	And Clico no botao "CHANGE"
	And Vejo o text view "Password updated" 
	And Clico back
	And Clico no botao "BACK"
	And Faço logout
	Then Fecho a aplicacao

Scenario: Apagar my exposure
	Given Eu inicio a aplicacao
	When Faço login "testaccount@mail.com" "123456789"
	And Clico no botao "DELETE MY EXPOSORE DATA" 
	And Vejo o text view "Account data deleted" 
	And Clico back
	And Faço logout
	Then Fecho a aplicacao
