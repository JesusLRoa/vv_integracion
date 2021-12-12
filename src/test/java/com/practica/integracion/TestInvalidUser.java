package com.practica.integracion;

import com.practica.integracion.DAO.AuthDAO;
import com.practica.integracion.DAO.GenericDAO;
import com.practica.integracion.DAO.User;
import com.practica.integracion.manager.SystemManager;
import com.practica.integracion.manager.SystemManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestInvalidUser {

	@Mock
	private static AuthDAO mockAuthDao;
	@Mock
	private static GenericDAO mockGenericDao;

	private User invalidUser;
	private SystemManager manager;
	private InOrder ordered;

	@BeforeEach
	void init() throws Exception {

		invalidUser = new User("1","Ana","Lopez","Madrid", new ArrayList<Object>(Arrays.asList(1, 2)));
		when(mockAuthDao.getAuthData(invalidUser.getId())).thenReturn(null);

		manager = new SystemManager(mockAuthDao, mockGenericDao);
		ordered = inOrder(mockAuthDao, mockGenericDao);

	}

	@Test
	public void testStartRemoteSystem() throws Exception {
		String validId = "12345";
		ArrayList<Object> lista = new ArrayList<>(Arrays.asList("uno", "dos"));
		when(mockGenericDao.getSomeData(null, "where id=" + validId)).thenThrow(OperationNotSupportedException.class);

		assertThrows(SystemManagerException.class, () -> manager.startRemoteSystem(invalidUser.getId(), validId));

		ordered.verify(mockAuthDao).getAuthData(invalidUser.getId());
		ordered.verify(mockGenericDao).getSomeData(null, "where id=" + validId);
	}

	@Test
	public void testStopRemoteSystem() throws Exception {
		String validId = "12345";
		ArrayList<Object> lista = new ArrayList<>(Arrays.asList("uno", "dos"));
		when(mockGenericDao.getSomeData(null, "where id=" + validId)).thenThrow(OperationNotSupportedException.class);

		assertThrows(SystemManagerException.class, () -> manager.stopRemoteSystem(invalidUser.getId(), validId));

		ordered.verify(mockAuthDao).getAuthData(invalidUser.getId());
		ordered.verify(mockGenericDao).getSomeData(null, "where id=" + validId);
	}

	@Test
	public void testAddRemoteSystemInvalidUser() throws Exception {
		String remote = "remote";
		when(mockGenericDao.updateSomeData(null, remote)).thenThrow(OperationNotSupportedException.class);

		assertThrows(SystemManagerException.class, () -> manager.addRemoteSystem(invalidUser.getId(), remote));

		ordered.verify(mockAuthDao).getAuthData(invalidUser.getId());
		ordered.verify(mockGenericDao).updateSomeData(null, remote);
	}

	@Test
	public void testAddRemoteSystemInvalidRemote() throws Exception {
		String remote = "invalid remote";
		when(mockGenericDao.updateSomeData(invalidUser, remote)).thenReturn(false);

		Exception exception = assertThrows(SystemManagerException.class, () -> manager.addRemoteSystem(invalidUser.getId(), remote));
		assertEquals("cannot add remote", exception.getMessage());

		ordered.verify(mockAuthDao).getAuthData(invalidUser.getId());
		ordered.verify(mockGenericDao).updateSomeData(invalidUser, remote);
	}

	@Test
	public void testDeleteRemoteSystemInvalidUser() throws Exception {
		String remote = "remote";
		when(mockGenericDao.deleteSomeData(invalidUser, remote)).thenThrow(OperationNotSupportedException.class);

		assertThrows(SystemManagerException.class, () -> manager.deleteRemoteSystem(invalidUser.getId(), remote));

		ordered.verify(mockGenericDao).updateSomeData(invalidUser, remote);
	}

	@Test
	public void testDeleteRemoteSystemInvalidRemote() throws Exception {
		String remote = "invalid remote";
		when(mockGenericDao.deleteSomeData(invalidUser, remote)).thenReturn(false);

		Exception exception = assertThrows(SystemManagerException.class, () -> manager.deleteRemoteSystem(invalidUser.getId(), remote));
		assertEquals("cannot delete remote: does remote exists?", exception.getMessage());

		ordered.verify(mockGenericDao).updateSomeData(invalidUser, remote);
	}
}
