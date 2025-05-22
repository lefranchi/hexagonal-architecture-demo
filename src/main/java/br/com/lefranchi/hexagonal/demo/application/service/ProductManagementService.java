package br.com.lefranchi.hexagonal.demo.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.lefranchi.hexagonal.demo.application.port.input.ProductManagementUseCase;
import br.com.lefranchi.hexagonal.demo.application.port.input.command.CreateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.command.UpdateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.response.ProductResponse;
import br.com.lefranchi.hexagonal.demo.application.port.output.ProductEventPublisher;
import br.com.lefranchi.hexagonal.demo.application.port.output.ProductRepository;
import br.com.lefranchi.hexagonal.demo.domain.exception.ProductNotFoundException;
import br.com.lefranchi.hexagonal.demo.domain.model.Product;
import br.com.lefranchi.hexagonal.demo.domain.vo.Money;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;

import lombok.RequiredArgsConstructor;

/**
 * Implementação dos casos de uso de gerenciamento de produtos.
 * 
 * Esta classe representa a camada de aplicação na Arquitetura Hexagonal,
 * orquestrando operações do domínio e coordenando a comunicação entre
 * as portas de entrada (REST API) e as portas de saída (persistência, eventos).
 * 
 * Responsabilidades:
 * - Implementar as regras de aplicação (não confundir com regras de domínio)
 * - Coordenar transações
 * - Publicar eventos de domínio
 * - Mapear entre objetos de comando/resposta e entidades de domínio
 * 
 * @author Leandro Franchi
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductManagementService implements ProductManagementUseCase {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;
    private final ProductMapper productMapper;
    
    /**
     * Cria um novo produto.
     * 
     * @param command comando contendo os dados do produto a ser criado
     * @return resposta com os dados do produto criado
     */
    @Override
    public ProductResponse createProduct(CreateProductCommand command) {
        // Criar entidade de domínio a partir do comando
        Product product = Product.create(
            ProductId.generate(),
            command.getName(),
            new Money(command.getPrice())
        );
        
        // Persistir o produto
        Product savedProduct = productRepository.save(product);
        
        // Publicar evento de domínio
        eventPublisher.publishProductCreated(savedProduct);
        
        // Converter para resposta
        return productMapper.toResponse(savedProduct);
    }
    
    /**
     * Busca um produto por ID.
     * 
     * @param id identificador do produto
     * @return resposta com os dados do produto encontrado
     * @throws ProductNotFoundException se o produto não for encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse findProduct(ProductId id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        
        return productMapper.toResponse(product);
    }
    
    /**
     * Lista todos os produtos.
     * 
     * @return lista de respostas com todos os produtos
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll().stream()
            .map(productMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Atualiza um produto existente.
     * 
     * @param id identificador do produto a ser atualizado
     * @param command comando contendo os novos dados do produto
     * @return resposta com os dados do produto atualizado
     * @throws ProductNotFoundException se o produto não for encontrado
     */
    @Override
    public ProductResponse updateProduct(ProductId id, UpdateProductCommand command) {
        // Buscar produto existente
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        
        // Aplicar atualizações através do método de domínio
        product.update(
            command.getName(),
            command.getPrice() != null ? new Money(command.getPrice()) : null
        );
        
        // Persistir alterações
        Product savedProduct = productRepository.save(product);
        
        // Publicar evento de domínio
        eventPublisher.publishProductUpdated(savedProduct);
        
        // Converter para resposta
        return productMapper.toResponse(savedProduct);
    }
    
    /**
     * Remove um produto.
     * 
     * @param id identificador do produto a ser removido
     * @throws ProductNotFoundException se o produto não for encontrado
     */
    @Override
    public void deleteProduct(ProductId id) {
        // Verificar se o produto existe antes de tentar deletar
        if (!productRepository.findById(id).isPresent()) {
            throw new ProductNotFoundException(id);
        }
        
        // Remover produto
        productRepository.deleteById(id);
        
        // Publicar evento de domínio
        eventPublisher.publishProductDeleted(id);
    }
    
    /**
     * Ativa um produto.
     * 
     * Esta operação aplica as regras de negócio definidas no domínio
     * para ativação de produtos (ex: não pode ativar produto com preço negativo).
     * 
     * @param id identificador do produto a ser ativado
     * @return resposta com os dados do produto ativado
     * @throws ProductNotFoundException se o produto não for encontrado
     * @throws InvalidProductException se o produto não puder ser ativado
     */
    @Override
    public ProductResponse activateProduct(ProductId id) {
        // Buscar produto existente
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
            
        // Aplicar regra de negócio de ativação (delegada para o domínio)
        product.activate();
        
        // Persistir alteração
        Product savedProduct = productRepository.save(product);
        
        // Publicar evento de domínio
        eventPublisher.publishProductActivated(savedProduct);
        
        // Converter para resposta
        return productMapper.toResponse(savedProduct);
    }
    
    /**
     * Desativa um produto.
     * 
     * @param id identificador do produto a ser desativado
     * @return resposta com os dados do produto desativado
     * @throws ProductNotFoundException se o produto não for encontrado
     */
    @Override
    public ProductResponse deactivateProduct(ProductId id) {
        // Buscar produto existente
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
            
        // Aplicar regra de negócio de desativação (delegada para o domínio)
        product.deactivate();
        
        // Persistir alteração
        Product savedProduct = productRepository.save(product);
        
        // Publicar evento de domínio
        eventPublisher.publishProductDeactivated(savedProduct);
        
        // Converter para resposta
        return productMapper.toResponse(savedProduct);
    }
}