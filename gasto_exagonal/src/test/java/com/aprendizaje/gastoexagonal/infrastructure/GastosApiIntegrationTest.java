package com.aprendizaje.gastoexagonal.infrastructure;

import com.aprendizaje.gastoexagonal.infrastructure.adapter.out.persistence.GastoJpaEntity;
import com.aprendizaje.gastoexagonal.infrastructure.adapter.out.persistence.SpringDataGastoRepository;
import com.aprendizaje.gastoexagonal.domain.model.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GastosApiIntegrationTest {

    private static final String BASE = "/api/v1/gastos";
    private static final String BODY = """
            {"descripcion":"Compra","importe":10.50,"fecha":"2020-01-02","categoria":"ALIMENTACION"}
            """;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SpringDataGastoRepository repository;

    @Test
    void creaYDevuelveLocationSinCuerpoComoElOriginal() throws Exception {
        var result = mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(content().string(""))
                .andReturn();
        String location = result.getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Compra"))
                .andExpect(jsonPath("$.importe").value(10.50))
                .andExpect(jsonPath("$.categoria").value("ALIMENTACION"));
    }

    @Test
    void listaYFiltraPorCategoria() throws Exception {
        guardar("Compra", "10.50", "2020-01-02", Categoria.ALIMENTACION);
        guardar("Cine", "20.00", "2020-01-03", Categoria.OCIO);

        mvc.perform(get(BASE)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get(BASE).param("categoria", "OCIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].descripcion").value("Cine"));
    }

    @Test
    void actualizaYElimina() throws Exception {
        Long id = guardar("Anterior", "5.00", "2020-01-01", Categoria.OTROS);

        mvc.perform(put(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.descripcion").value("Compra"));
        mvc.perform(delete(BASE + "/" + id)).andExpect(status().isNoContent());
        mvc.perform(get(BASE + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void totalIncluyeAmbosExtremosYExcluyeFueraDelRango() throws Exception {
        guardar("Primero", "10.50", "2020-01-01", Categoria.OTROS);
        guardar("Ultimo", "20.00", "2020-01-31", Categoria.OTROS);
        guardar("Fuera", "99.00", "2020-02-01", Categoria.OTROS);

        mvc.perform(get(BASE + "/total").param("desde", "2020-01-01").param("hasta", "2020-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.desde").value("2020-01-01"))
                .andExpect(jsonPath("$.hasta").value("2020-01-31"))
                .andExpect(jsonPath("$.totalGastos").value(30.50));
    }

    @Test
    void totalSinResultadosEsCero() throws Exception {
        mvc.perform(get(BASE + "/total").param("desde", "2000-01-01").param("hasta", "2000-01-01"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalGastos").value(0));
    }

    @Test
    void devuelveCategorias() throws Exception {
        mvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0]").value("ALIMENTACION"));
    }

    @Test
    void devuelve404EnTodasLasOperacionesSobreAusente() throws Exception {
        mvc.perform(get(BASE + "/999999")).andExpect(status().isNotFound());
        mvc.perform(put(BASE + "/999999").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNotFound());
        mvc.perform(delete(BASE + "/999999")).andExpect(status().isNotFound());
    }

    @Test
    void validaDatosAlCrearYActualizarSinModificarElOriginal() throws Exception {
        String invalido = BODY.replace("10.50", "-1");
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));

        Long id = guardar("Anterior", "5.00", "2020-01-01", Categoria.OTROS);
        mvc.perform(put(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/" + id)).andExpect(jsonPath("$.descripcion").value("Anterior"));
    }

    @Test
    void rechazaCamposObligatoriosYFechaFutura() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.replace("2020-01-02", "2999-01-01")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaJsonEnumFechasEIdentificadoresMalFormados() throws Exception {
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content("null"))
                .andExpect(status().isBadRequest());
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BODY.replace("ALIMENTACION", "INVALIDA")))
                .andExpect(status().isBadRequest());
        mvc.perform(get(BASE).param("categoria", "INVALIDA")).andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/abc")).andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/0")).andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/total").param("desde", "ayer").param("hasta", "2020-01-01"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(BASE + "/total")).andExpect(status().isBadRequest());
    }

    @Test
    void rangoInvertidoEs400YNo404() throws Exception {
        mvc.perform(get(BASE + "/total").param("desde", "2020-02-01").param("hasta", "2020-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    private Long guardar(String descripcion, String importe, String fecha, Categoria categoria) {
        return repository.saveAndFlush(new GastoJpaEntity(null, descripcion, new BigDecimal(importe),
                LocalDate.parse(fecha), categoria)).getId();
    }
}
