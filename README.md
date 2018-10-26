# Sobre
Roteiro de uma apresentação interativa que fiz.
Sinta-se livre para fazer PR's corrigindo os todo's.

# Setup
Antes de começar, instale o `lein`.
http://leiningen.org/
Recomendo usar `jvm8` para evitar problemas de compatibilidade
```bash
lein version
Leiningen 2.8.1 on Java 1.8.0_192 OpenJDK 64-Bit Server VM
```
Suba um banco Postgres com usuario/senha Postgres. Exemplo em docker
```bash
docker run -p 5432:5432 postgres:alpine
```
Clone o projeto, entre na pasta e abra o repl do lein.
Recomendado uso de um repl integrado ao editor de texto.
TODO: Configurar project.clj para lein repl usar rebel-readline
TODO: Configurar deps.clj
https://github.com/bhauman/rebel-readline (Fazer PR por favor)
```bash
git clone https://github.com/souenzzo/todo-demo.git
cd todo-demo
lein with-profile +dev,+client,+server repl
```

# Roteiro
- Brinque com EDN:
Tente jogar em seu REPL as estruturas de dados. Qualquer valor pode ficar em qualquer lugar
sempre use o `'` no inicio da estrutura de dado
```clojure
'{:chave :valor}
'[:valor :valor]
'
```
- Explicar clojure
As listas do `edn` são interpretadas como chammadas de função. Use a função `get` para pegar valores das estruturas
```clojure
(get {:name "Enzzo"} :name)
(get {:name "Enzzo"} :no-name)
(get [:a :b :c] 2)
```
- Fazer query no database
Chame a função `install-db-schema` depois faça uma query no banco.
Digite apenas `db` no repl para ver o valor da variavel `db`, já preparada
```clojure
(install-db-schema)
db
(j/query db "SELECT * FROM todo")
```
- Fazer insert no database
Insira dads no db, faça outras operações
a API do JDBC está aqui
http://clojure-doc.org/articles/ecosystem/java_jdbc/using_sql.html
```clojure
(j/insert! db :todo {:data "abc123"})
```
Você pode ver o resultado no Postgres usando `psql -h localhost -U postgres` no terminal
- usar handler HTTP para fazer query no database
Vá para o namespace `todo-server.core` e chame manualmente os handlers http
Edite o status da `list-todo` para retornar 201
Caso seu editor de texto não tenha operação de "enviar para o repl", vc pode usar a primitiva `load`
Recomendo fortemente usar um editor com suporte a reload nativo
```
(in-ns 'todo-server.core)
(list-todo {}) ;; deve retornar 200
;; edite para 201
(load "core")
(list-todo {}) ;; deve retornar 201
```

- subir servidor HTTP
`(-main)`
- fazer CURL
`curl localhost:8080`
- editar handler HTTP
- fazer CURL
- Iniciar build cljs
- Mostrar site
- Mostrar cards
- Mostrar teste via cards
- Reload no cards
- Mostrar teste via JVM
- Mostrar testes do backend
- Mostrar site, reload no site
- REPL CLJS: (- 1 "1")
- Mudar do cliente estado pelo REPL
- Chamar função de view do repl
- Chamar função de IO do REPL
- Mudar função view via REPL
