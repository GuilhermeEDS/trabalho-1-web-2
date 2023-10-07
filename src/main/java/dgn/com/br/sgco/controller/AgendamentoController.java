package dgn.com.br.sgco.controller;

import dgn.com.br.sgco.dto.AgendamentoDTO;
import dgn.com.br.sgco.dto.AgendamentoDentistaDTO;
import dgn.com.br.sgco.entity.Agendamento;
import dgn.com.br.sgco.entity.Usuario;
import dgn.com.br.sgco.enumeration.FormaPagamento;
import dgn.com.br.sgco.enumeration.TipoAgendamento;
import dgn.com.br.sgco.service.AgendamentoService;
import dgn.com.br.sgco.service.DentistaService;
import dgn.com.br.sgco.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AgendamentoController {
    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DentistaService dentistaService;

    @GetMapping("/agendamento")
    public String paginaAgendamento(@NonNull Model model) {
        AgendamentoDTO agendamentoDTO = new AgendamentoDTO();
        model.addAttribute("agendamentoDTO", agendamentoDTO);
        model.addAttribute("formasPagamento", FormaPagamento.values());
        model.addAttribute("tiposAgendamento", TipoAgendamento.values());
        model.addAttribute("dentistas", dentistaService.todos());
        return "agendamento";
    }

    @PostMapping("/agendamento")
    public String agendar(final @Valid AgendamentoDTO agendamentoDto, @NonNull BindingResult result, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String cpf = auth.getName();
        Usuario usuario = usuarioService.porCpf(cpf).get();

        if (result.hasErrors()) {
            model.addAttribute("agendamentoDto", agendamentoDto);
            model.addAttribute("formasPagamento", FormaPagamento.values());
            model.addAttribute("tiposAgendamento", TipoAgendamento.values());
            model.addAttribute("dentistas", dentistaService.todos());
            return "agendamento";
        }

        Agendamento agendamento = agendamentoService.agendar(agendamentoDto, usuario.getPaciente());
        return "redirect:/";
    }

    @GetMapping("/agendamento/{id}")
    public String paginaConfirmarAgendamento(@PathVariable("id") long id, @NonNull Model model) {
        AgendamentoDentistaDTO agendamentoDTO = new AgendamentoDentistaDTO();
        model.addAttribute("agendamentoDTO", agendamentoDTO);
        model.addAttribute("agendamento", agendamentoService.porId(id).get());
        return "agendamentoDentista";
    }

    @PostMapping("/agendamento/{id}")
    public String confirmarAgendamento(@PathVariable("id") long id, final @Valid AgendamentoDentistaDTO agendamentoDto, @NonNull BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("agendamentoDto", agendamentoDto);
            model.addAttribute("formasPagamento", FormaPagamento.values());
            model.addAttribute("tiposAgendamento", TipoAgendamento.values());
            model.addAttribute("dentistas", dentistaService.todos());
            return "agendamento";
        }

        Agendamento agendamento = agendamentoService.confirmarAgendamento(id, agendamentoDto.getObservacoesDentista(), agendamentoDto.getHoraFim());
        return "redirect:/";
    }

}
