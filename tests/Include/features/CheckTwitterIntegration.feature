Feature: 
  Como utilizador autenticado
  Quero partilhar para o twiter um sumario da informação da qualidade do ar dados de temperatura e humidade mais recentes num local
  Para que as outras pessoas possam saber a qualidade do ar no IPL

Scenario: Utilizador Nao Autenticado partilhar
	Given Eu inicio a aplicacao
  When Nao vejo o botao "Share"
  Then Fecho a aplicacao

Scenario: Utilizador Autenticado partilhar
	Given Eu inicio a aplicacao
  When Clico no botao "PROFILE"
  And Clico no botao "LOGIN"
  And Preencho "testaccount@mail.com" no campo "Email"
  And Preencho "123456789" no campo "Password"
  And Clico no botao "LOGIN"
  And Clico no botao "Dashboard"
  And Clico no botao "Share"
  And Vejo a ImageView "Share" 
  And Faço logout
  Then Fecho a aplicacao
