# Sobre
Roteiro de uma apresentação interativa que fiz.
Sinta-se livre para fazer PR's corrigindo os todo's.

# Setup
Antes de começar, instale o `clojure`.
- https://clojure.org/guides/getting_started
```bash
clj -Sdescribe
{:version "1.10.1.727"
 :config-files ["/usr/share/clojure/deps.edn" "/home/souenzzo/.clojure/deps.edn" "deps.edn" ]
 :config-user "/home/souenzzo/.clojure/deps.edn"
 :config-project "deps.edn"
 :install-dir "/usr/share/clojure"
 :config-dir "/home/souenzzo/.clojure"
 :cache-dir ".cpcache"
 :force false
 :repro false
 :main-aliases ""
 :repl-aliases ""}
```
Suba um banco Postgres com usuario/senha Postgres. Exemplo em docker
```bash
docker run -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:alpine
```
Clone o projeto, entre na pasta e abra o repl do clojure.
Recomendado uso de um repl integrado ao editor de texto.
TODO: Configurar repl usar rebel-readline
TODO: Configurar deps.clj
https://github.com/bhauman/rebel-readline (Fazer PR por favor)
```bash
git clone https://github.com/souenzzo/todo-demo.git
cd todo-demo
clj -A:cljs:dev
```

**ATENÇÃO**: Uma vez com o REPL aberto, você não deve mais fechar ele.

# Roteiro
- Brinque com EDN:
Tente jogar em seu REPL as estruturas de dados. Qualquer valor pode ficar em qualquer lugar
sempre use o `'` no inicio da estrutura de dado
```clojure
'{:chave :valor}
'[:valor :valor]
'{[:seja] (criativo )}
```
A especificação do EDN está aqui: https://github.com/edn-format/edn
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
(j/execute! db ["SELECT * FROM todo"])
```
- Fazer insert no database
Insira dads no db, faça outras operações
a API do JDBC está aqui
http://clojure-doc.org/articles/ecosystem/java_jdbc/using_sql.html
```clojure
(j/execute! db ["INSERT INTO todo (data) VALUES(?)" "Olá mundo!"])
(j/execute! db ["SELECT * FROM todo"])
```
Você pode ver o resultado no Postgres usando `psql -h localhost -U postgres` no terminal
- usar handler HTTP para fazer query no database
Vá para o namespace `todo-server.core` usando `in-ns` e chame manualmente os handlers http
Edite o status da `list-todo` para retornar 201
Caso seu editor de texto não tenha operação de "enviar para o repl", vc pode usar a primitiva `load`
Recomendo fortemente usar um editor com suporte a reload nativo
```
(in-ns 'todo-server.core)
(list-todo {}) ;; deve retornar 200
;; no arquivo src/todo_server/core.clj, usando qualquer editor, linha 22.
;; edite de `:status 200` para `:status 201`
(require 'todo-server.core :reload) ;; Esse comando irá recarregar este arquivo no REPL
(list-todo {}) ;; deve retornar 201
```

- subir servidor HTTP
Agora vamos subir o servidor HTTP. Você pode ver proximo da linha 73 suas rodas
`(-main)`
- fazer CURL/POSTMAN
Usando o cliente HTTP  de sua preferencia (meus exemplos serão em `curl`, mas muita gente usa o POSTMAN), 
faça um `GET` na porta `8080` com o caminho `/todos`
```bash
curl localhost:8080/todo
[{"todo\/text":"Ol\u00e1 mundo!","todo\/id":1}]
```

Obtemos o resultado como esperado
- editar handler HTTP
- fazer CURL
- Iniciar build cljs
`(user/start)`
- Mostrar site `http://localhost:3449`
- Mostrar cards `http://localhost:3449/cards.html`
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
