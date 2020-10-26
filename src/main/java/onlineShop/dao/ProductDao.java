package onlineShop.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import onlineShop.model.Product;

//添加删除修改
//Repository望别的模块引用我
@Repository
public class ProductDao {

	@Autowired
	private SessionFactory sessionFactory;

	public void addProduct(Product product) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			//多笔交易；两条记录需要更新
			session.beginTransaction();
			session.save(product); 
			//commit: 对应该java insert 到table上；确认操作
			session.getTransaction().commit(); 
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			if (session != null) {
				session.close(); //关闭session
			}
		}
	}

	public void deleteProduct(int productId) {
		Session session = null;
		try {
		    //是打开一个新的session对象，而且每次使用都是打开一个新的session
			//自动close;
			//session 只在try catch 里。
			session = sessionFactory.openSession();
			session.beginTransaction();
			Product product = (Product) session.get(Product.class, productId);
			session.delete(product);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	//updated product
	public void updateProduct(Product product) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
//			session.beginTransaction();
			session.saveOrUpdate(product);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	//返回某一个具体的商品信息
	public Product getProductById(int productId) {
		try (Session session = sessionFactory.openSession()) {
			Product product = (Product) session.get(Product.class, productId);
			return product;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//go over all of the products; 全表搜索
	public List<Product> getAllProducts() {
		List<Product> products = new ArrayList<Product>();
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
			Root<Product> root = criteriaQuery.from(Product.class);
			criteriaQuery.select(root);
			products = session.createQuery(criteriaQuery).getResultList();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return products;
	}
}

//CriteriaBuilder 封装
