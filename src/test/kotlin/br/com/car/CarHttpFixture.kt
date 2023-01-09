package br.com.car

import br.com.car.domain.http.CarHttp

object CarHttpFixture {
    fun getCarHttpFixture() = CarHttp("VW", "gol", 2015)
}