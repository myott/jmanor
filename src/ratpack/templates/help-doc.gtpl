layout 'layout.gtpl',
    title: title,
    bodyContents: contents {
      h1('JManor API Documentation')
      p {
        yield 'Some API docs'
      }
    }