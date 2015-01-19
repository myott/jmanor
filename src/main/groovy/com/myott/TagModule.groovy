package com.myott

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Scopes

/**
 * Created by Matt on 1/18/15.
 */
class TagModule implements Module {

  @Override
  void configure(Binder binder) {
    binder.bind(TagDbCommandService).in(Scopes.SINGLETON)
  }
}
