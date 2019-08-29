package br.gov.tcu.zello;

import java.util.List;

public class DtoRespostaZello {
    private String destinatario;
    private List<String> respostas;

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public List<String> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<String> respostas) {
        this.respostas = respostas;
    }
}
