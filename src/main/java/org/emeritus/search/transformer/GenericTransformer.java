package org.emeritus.search.transformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GenericTransformer.
 *
 * @param <ENTITY> the generic type
 * @param <DTO> the generic type
 */
public abstract class GenericTransformer<ENTITY, DTO> {

  /**
   * To entity.
   *
   * @param dto the dto
   * @return the entity
   */
  public abstract ENTITY toEntity(DTO dto);

  /**
   * To DTO.
   *
   * @param entity the entity
   * @return the dto
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public abstract DTO toDTO(ENTITY entity) throws IOException;

  /**
   * To dto.
   *
   * @param entities the entities
   * @return the list
   */
  public List<DTO> toDto(List<ENTITY> entities) {

    List<DTO> dtos = new ArrayList<>();
    if (!entities.isEmpty()) {
      entities.stream().forEach(entity -> {
        try {
          dtos.add(toDTO(entity));
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
    return dtos;
  }

  /**
   * To entity.
   *
   * @param dtos the dtos
   * @return the list
   */
  public List<ENTITY> toEntity(List<DTO> dtos) {

    List<ENTITY> dtentityList = new ArrayList<>();
    if (!dtos.isEmpty()) {
      dtos.stream().forEach(dto -> dtentityList.add(toEntity(dto)));
    }
    return dtentityList;
  }
}
