package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.web.mappers.BlueprintMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerBlueprintServiceImpl implements CustomerBlueprintService {
    private final BlueprintRepository blueprintRepository;
    private final BlueprintMapper blueprintMapper;
    private final CustomerAuthenticatedBlueprintAccessor authenticatedBlueprintAccessor;

    @Override
    public BlueprintDto authorizedGetBlueprintById(Long id) {
        return blueprintMapper.toDto(
                blueprintRepository.findById(id).orElse(null));
    }

    @Override
    public Optional<BlueprintDto> getBlueprintById(Long id) {
        return Optional.ofNullable(blueprintMapper.toDto(
                authenticatedBlueprintAccessor.findById(id).orElse(null)));
    }

    @Override
    public Set<BlueprintDto> listBlueprints(Long storeId, Set<Long> ids) {
        final Set<Blueprint> blueprints;
        if (ids == null) {
            blueprints = blueprintRepository.findByStore_Id(storeId);
        } else {
            blueprints = blueprintRepository.findByStore_IdAndIdIn(storeId, ids);
        }
        return blueprints.stream()
                .map(blueprintMapper::toDto).collect(Collectors.toSet());
    }
}
