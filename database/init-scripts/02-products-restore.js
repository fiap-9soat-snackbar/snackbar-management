// Conectar ao MongoDB
db = db.getSiblingDB('snackbar');

// Inserir registros na colection 'products'
db.products.insertMany([
    {
      name: 'Hambúrguer',
      category: 'Lanche',
      description: 'Hambúrguer artesanal 160g, servido com pão de brioche, alface e tomate.',
      price: 22.00,
      cookingTime: 10,
      image: 'https://example.com/hamburguer.jpg'
  },
  {
      name: 'Cheesebúrguer',
      category: 'Lanche',
      description: 'Hambúrguer artesanal 160g, servido com pão de brioche e queijo prato.',
      price: 25.00,
      cookingTime: 10,
      image: 'https://example.com/cheeseburguer.jpg'
  },
  {
      name: 'Hot Dog de Salsicha',
      category: 'Lanche',
      description: 'Hot dog  servido com duas salsichas, molho, batata palha.',
      price: 20.00,
      cookingTime: 10,
      image: 'https://example.com/hotdog-salsicha.jpg'
  },
  {
      name: 'Pizza de Muçarela',
      category: 'Lanche',
      description: 'Pizza de muçarela individual com tomate e manjericão.',
      price: 25.00,
      cookingTime: 12,
      image: 'https://example.com/pizza-mucarela.jpg'
  },
  {
      name: 'Pizza de Calabresa',
      category: 'Lanche',
      description: 'Pizza de calabresa individual com queijo muçarela e cebolas.',
      price: 25.00,
      cookingTime: 12,
      image: 'https://example.com/pizza-calabresa.jpg'
  },
  {
      name: 'Salada Caesar',
      category: 'Lanche',
      description: 'Salada de alface americana, peito de frango em tiras, queijo parmesão, croutons e molho caesar.',
      price: 20.00,
      cookingTime: 5,
      image: 'https://example.com/salada-caesar.jpg'
  },
  {
      name: 'Batata frita Grande',
      category: 'Acompanhamento',
      description: 'Porção grande de batatas fritas crocantes.',
      price: 15.00,
      cookingTime: 8,
      image: 'https://example.com/batata-frita-grande.jpg'
  },
  {
      name: 'Batata frita Media',
      category: 'Acompanhamento',
      description: 'Porção media de batatas fritas crocantes.',
      price: 12.00,
      cookingTime: 8,
      image: 'https://example.com/batata-frita-media.jpg'
  },
  {
      name: 'Batata frita Pequena',
      category: 'Acompanhamento',
      description: 'Porção pequena de batatas fritas crocantes.',
      price: 10.00,
      cookingTime: 8,
      image: 'https://example.com/batata-frita-pequenaa.jpg'
  },
  {
      name: 'Chips de Batata Doce',
      category: 'Acompanhamento',
      description: 'Pacote de chips de batata doce.',
      price: 8.00,
      cookingTime: 1,
      image: 'https://example.com/chips-batata-doce.jpg'
  },
  {
      name: 'Coca-Cola 350ml',
      category: 'Bebida',
      description: 'Refrigerante Coca-Cola lata de 350ml.',
      price: 8.00,
      cookingTime: 1,
      image: 'https://example.com/coca-lata-350ml.jpg'
  },
  {
      name: 'Coca-Cola 500ml',
      category: 'Bebida',
      description: 'Refrigerante Coca-Cola copo de 500ml.',
      price: 10.00,
      cookingTime: 1,
      image: 'https://example.com/coca-copo-500ml.jpg'
  },
  {
      name: 'Guaraná Antarctica 350ml',
      category: 'Bebida',
      description: 'Refrigerante Guaraná Antarctica lata de 350ml.',
      price: 8.00,
      cookingTime: 1,
      image: 'https://example.com/guarana-lata-350ml.jpg'
  },
  {
      name: 'Guaraná Antarctica 500ml',
      category: 'Bebida',
      description: 'Refrigerante Guaraná Antarctica copo de 500ml.',
      price: 10.00,
      cookingTime: 1,
      image: 'https://example.com/guarana-copo-500ml.jpg'
  },
  {
      name: 'Suco Natural 350ml',
      category: 'Bebida',
      description: 'Suco natural em copo de 350ml.',
      price: 8.00,
      cookingTime: 1,
      image: 'https://example.com/suco-copo-350ml.jpg'
  },
  {
      name: 'Suco Natural 500ml',
      category: 'Bebida',
      description: 'Suco natural em copo de 500ml.',
      price: 10.00,
      cookingTime: 1,
      image: 'https://example.com/suco-copo-500ml.jpg'
  },
  {
      name: 'Água Mineral 500ml',
      category: 'Bebida',
      description: 'Água mineral em garrafa de 500ml.',
      price: 5.00,
      cookingTime: 1,
      image: 'https://example.com/agua-garrafa-500ml.jpg'
  },
  {
      name: 'Sundae de Chocolate',
      category: 'Sobremesa',
      description: 'Sorvete de baunilha com calda de chocolate.',
      price: 10.00,
      cookingTime: 5,
      image: 'https://example.com/sundae-chocolate.jpg'
  },
  {
      name: 'Sundae de Morango',
      category: 'Sobremesa',
      description: 'Sorvete de baunilha com calda de morango.',
      price: 10.00,
      cookingTime: 5,
      image: 'https://example.com/sundae-morango.jpg'
  },
  {
      name: 'Pudim',
      category: 'Sobremesa',
      description: 'Pudim de leite.',
      price: 12.00,
      cookingTime: 2,
      image: 'https://example.com/pudim.jpg'
  },
  {
      name: 'Brownie',
      category: 'Sobremesa',
      description: 'Brownie de chocolate.',
      price: 12.00,
      cookingTime: 2,
      image: 'https://example.com/brownie.jpg'
  }
  ]);
  
  print("Collection 'products' restored.");