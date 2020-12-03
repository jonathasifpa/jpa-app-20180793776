package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import entidades.Cliente;
import entidades.Pedido;
import model.PedidoVO;
import utilitarios.FabricaConexao;
import utilitarios.GeradorID;

public class PedidoDAO {
	private EntityManager em = FabricaConexao.getConexao();
	/**
	 * Inclui um objeto Pedido na base de dados
	 */
	public boolean incluir(PedidoVO vo) {
		// utiliza um objeto de transferência (VO)
		Pedido p = new Pedido();
		p.setId(GeradorID.geraNumeroID());
		p.setNomeProduto(vo.getNomeProduto());
		p.setQuantidade(vo.getQuantidade());
		p.setValorTotal(vo.getValorTotal());
		p.setIdCliente(findClienteById(vo.getIdCliente()));
		
		vo.setId(p.getId()); // repassa o ID para o VO
		
		em.getTransaction().begin();
		em.persist(p);
		em.getTransaction().commit();
		return true;
	}

	/**
	 * Pesquisa e retorna um Pedido baseado na chave "id"
	 */
	public Pedido findById(Integer id) {
		Pedido p = em.find(Pedido.class, id);
		if (p == null) {
			throw new EntityNotFoundException("Pedido não localizado pelo ID " + id);
		}
		return p;
	}

	/**
	 * Recupera todos os pedidos da base de dados 
	 * @return uma Lista de pedidos
	 */
	public List<Pedido> getPedidos(int clienteId) {
		Query  query = em.createQuery("select p from Pedido p where id_cliente = :id_cliente", Pedido.class);
		query.setParameter("id_cliente", clienteId);
		return query.getResultList();
	}

	/**
	 * Este método realiza o "update" dos dados do pedido.
	 * @param vo
	 */
	public boolean atualiza(PedidoVO vo) {
		// recupera o objeto persistente
		Pedido p = this.findById(vo.getId());
		p.setNomeProduto(vo.getNomeProduto());
		p.setQuantidade(vo.getQuantidade());
		p.setValorTotal(vo.getValorTotal());


		em.getTransaction().begin();
		em.merge(p); // merge -> faz o "update" no objeto persistente
		em.getTransaction().commit();
		return true;
	}
	
	// deleta usando o ID
	public boolean delete(int id) {
		// recupera o objeto persistente
		Pedido p = this.findById(id);
		if (p == null)
			return false;
		em.getTransaction().begin();
		em.remove(p);  // remove -> faz o "delete" no objeto persistente
		em.getTransaction().commit();
		return true;
	}
		
	public boolean deletePedidosCliente(int clienteId) {
		Query query = em.createQuery("delete Pedido where id_cliente = :id_cliente");
		query.setParameter("id_cliente", clienteId);
		em.getTransaction().begin();
		query.executeUpdate();
		em.getTransaction().commit();
		return true;
	}
	
	public Cliente findClienteById(Integer id) {
		Cliente c = em.find(Cliente.class, id);
		if (c == null) {
			throw new EntityNotFoundException("Cliente não localizado pelo ID " + id);
		}
		return c;
	}
}
