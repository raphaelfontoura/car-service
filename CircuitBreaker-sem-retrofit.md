### Para saber mais: Circuit Breaker sem Retrofit

Nós vimos que o circuit breaker é um passo importantíssimo no trabalho de manter nossos serviços mais resilientes.

Junto ao Retrofit, vimos que ambos possuem uma integração poderosa, com a qual podemos integrar o circuit breaker a chamadas de API que o utilizem, como client HTTP, de forma bem simples.

Porém, como já havíamos falado anteriormente, podemos participar de projetos nos quais essas *"facilidades"* não são possíveis, por exemplo, um projeto que não use o Retrofit e consequentemente, perdemos o poder dessas integrações.

Não é por não usarmos o Retrofit que não devemos usar circuit breaker. Como já foi dito, ele é um recurso poderoso a ser aplicado sempre que possível. Às vezes, trocar o client HTTP também não é uma opção, pois pode levar muito tempo ou aumentar a complexidade, por exemplo. Então como fazer para usar o circuit breaker sem o Retrofit?

O primeiro ponto da questão é que não haverá mudanças na classe de configuração, declaramos as informações de quando o circuito deve abrir, quando deve ficar no estado de HALF OPEN, quando deve fechar e assim por diante.

```kotlin
@Configuration
class CircuitBreakerConfiguration {

    fun getConfiguration() =
        CircuitBreakerConfig
            .custom()
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(10)
            .slowCallRateThreshold(70.0f)
            .slowCallDurationThreshold(Duration.ofSeconds(2))
            .waitDurationInOpenState(Duration.ofSeconds(5000))
            .permittedNumberOfCallsInHalfOpenState(10)
            .writableStackTraceEnabled(false)
            .build()

    fun getCircuitBreaker() =
        CircuitBreakerRegistry.of(getConfiguration())
            .circuitBreaker("CIRCUIT-BREAKER")
}
```

Como percebemos, a configuração é a mesma usada no projeto car-service.

Agora vamos configurar o serviço que deve usar o circuit breaker. Para fins de teste, não usaremos uma API real como nos vídeos, vamos usar funções com o intuito de mostrar o código sem Retrofit.

```kotlin
fun main() {
    val ds = circuitBreakerConfiguration.getCircuitBreaker().decorateSupplier { listTest() }
    for(i in 1 .. 20) {
        try {
            print(ds.get())
        } catch(e: Exception){
            e.printStackTrace()
        }
    }
}

fun listTest(): String {
    try {
        Thread.sleep(5000)
    } catch(e: InterruptedException) {
        e.printStackTrace()
    }
    return "Ola mundo!!"
}
```
Começaremos pela função `listTest( )`. O objetivo dela é apenas retornar a tradicional frase *"Ola mundo"*, porém, foi inserido um `Thread.sleep` de 5 segundos, forçando a requisição para a função a levar este tempo até o retorno.

Pela configuração do circuit breaker, quando forem feitas 10 requisições, 70% delas estarão acima do que seria considerado `SlowCallDuration`, que se trata de 2 segundos. Isso forçará a abrir o circuito.

Agora vamos para a função principal, na qual temos novos detalhes. De acordo com a documentação do Resilience4j, ele provê high order functions, que são decorators, envolvendo qualquer interface funcional (ou seja, com apenas um método abstrato), expressões lambda ou method references com circuit breaker.

Por causa disso, chamamos a função `listTest( )` dentro de uma expressão lambda `decorateSupplier (decorator)`. O Kotlin consegue, por debaixo dos panos, converter o retorno do trecho `circuitBreakerConfiguration.getCircuitBreaker().decorateSupplier { listTest() } para um supplier, graças ao SAM Conversions.

Como o retorno é um supplier, uma interface funcional que possui apenas um método `get( ), podemos chamá-lo para fazer a requisição à função com o circuit breaker. Vamos usar um for com um range de 1 até 20, o suficiente para a análise do circuito e consequentemente, para a abertura dele.

Se executarmos essa função, vamos ver que após alguns retornos *"Ola mundo"*, passamos a receber a exceção **CallNotPermitedException**, mostrando que o nosso circuito está funcional.

Como podemos observar, precisamos escrever um pouco mais de código quando não utilizamos o Retrofit, porém o resultado é o mesmo.

Para o código funcionar, será necessário injetar a classe de configuração ou utilizar a configuração no mesmo arquivo.