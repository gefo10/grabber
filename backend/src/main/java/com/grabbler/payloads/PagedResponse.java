package com.grabbler.payloads;

import java.time.LocalDateTime;
import java.util.List;

public class PagedResponse<T> {
  private List<T> content;
  private PaginationMetadata metadata;
  private LocalDateTime timestamp;

  public PagedResponse(List<T> content, PaginationMetadata metadata) {
    this.content = content;
    this.metadata = metadata;
    this.timestamp = LocalDateTime.now();
  }
}
