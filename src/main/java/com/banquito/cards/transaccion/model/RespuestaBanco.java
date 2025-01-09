package com.banquito.cards.transaccion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RespuestaBanco {
    private DatosRespuesta datos;
    private MetaRespuesta meta;
    private ErrorRespuesta error;
    private int httpStatus;

    public static class DatosRespuesta {
        private String mensaje;
        
        public String getMensaje() {
            return mensaje;
        }
        
        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }

    public static class MetaRespuesta {
        private String version;
        private String servidor;
        private String documentacion;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getServidor() {
            return servidor;
        }

        public void setServidor(String servidor) {
            this.servidor = servidor;
        }

        public String getDocumentacion() {
            return documentacion;
        }

        public void setDocumentacion(String documentacion) {
            this.documentacion = documentacion;
        }
    }

    public static class ErrorRespuesta {
        private String codigo;
        private String mensaje;
        private String detalles;
        private List<String> errores;
        
        public String getCodigo() {
            return codigo;
        }
        
        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }
        
        public String getMensaje() {
            return mensaje;
        }
        
        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
        
        public String getDetalles() {
            return detalles;
        }
        
        public void setDetalles(String detalles) {
            this.detalles = detalles;
        }
        
        public List<String> getErrores() {
            return errores;
        }
        
        public void setErrores(List<String> errores) {
            this.errores = errores;
        }
    }

    public boolean isSuccess() {
        return httpStatus == 201;
    }

    public String getMessage() {
        if (error != null) {
            return error.getMensaje();
        }
        return datos != null ? datos.getMensaje() : null;
    }

    public DatosRespuesta getDatos() {
        return datos;
    }

    public void setDatos(DatosRespuesta datos) {
        this.datos = datos;
    }

    public MetaRespuesta getMeta() {
        return meta;
    }

    public void setMeta(MetaRespuesta meta) {
        this.meta = meta;
    }

    public ErrorRespuesta getError() {
        return error;
    }

    public void setError(ErrorRespuesta error) {
        this.error = error;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
} 