package ru.epam.spring.cinema.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.UserRepository;
import ru.epam.spring.cinema.repository.filter.UserFilter;
import ru.epam.spring.cinema.service.UserService;
import ru.epam.spring.cinema.service.UserServiceImpl;

public class TestUserService {

	private UserService userService;
	private UserRepository userRepositoryMock;

	@Before
	public void init() {
		userRepositoryMock = Mockito.mock(UserRepository.class);

		UserServiceImpl userServiceImpl = new UserServiceImpl();
		userServiceImpl.setAuditoriumRepository(userRepositoryMock);
		userService = userServiceImpl;
	}

	@Test
	public void getByIdReturnsUserIfFound() {
		User fakeUser = new User();
		Mockito.when(userRepositoryMock.getById(Mockito.anyLong())).thenReturn(fakeUser);

		User user = userService.getById(1L);
		assertNotNull(user);

		Mockito.verify(userRepositoryMock).getById(1L);
	}

	@Test
	public void getByIdReturnsNullIfNotFound() {
		Mockito.when(userRepositoryMock.getById(Mockito.anyLong())).thenReturn(null);

		User user = userService.getById(1L);
		assertNull(user);

		Mockito.verify(userRepositoryMock).getById(1L);
	}

	@Test
	public void getAllReturnsCollectionOfUsers() {
		List<User> fakeUserCollection = new ArrayList<User>();
		fakeUserCollection.add(new User());
		Mockito.when(userRepositoryMock.getAll()).thenReturn(fakeUserCollection);

		Collection<User> users = userService.getAll();
		assertNotNull(users);
		assertEquals(1, users.size());

		Mockito.verify(userRepositoryMock).getAll();
	}

	@Test
	public void savePutsObjectIntoRepository() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Kross");
		user.setEmail("John_Kross@xxx.com");

		User fakeUser = new User();
		fakeUser.setId(123L);

		Mockito.when(userRepositoryMock.save(Mockito.any(User.class))).thenReturn(fakeUser);

		User resultUser = userService.save(user);
		assertNotNull(resultUser);
		assertNotNull(resultUser.getId());
		assertEquals(123, resultUser.getId().longValue());

		ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
		Mockito.verify(userRepositoryMock).save(userArgument.capture());
		assertNotNull(userArgument.getValue());
		assertEquals("John", userArgument.getValue().getFirstName());
		assertEquals("Kross", userArgument.getValue().getLastName());
		assertEquals("John_Kross@xxx.com", userArgument.getValue().getEmail());
	}

	@Test
	public void removeRemovesObjectFromRepository() {
		User user = new User();
		user.setId(123L);
		user.setFirstName("John");
		user.setLastName("Kross");
		user.setEmail("John_Kross@xxx.com");

		Mockito.doNothing().when(userRepositoryMock).removeById(Mockito.anyLong());

		userService.remove(user);

		Mockito.verify(userRepositoryMock).removeById(123L);
	}

	@Test(expected=IllegalArgumentException.class)
	public void removeThrowsExceptionIfIdNotSpecified() {
		User user = new User();
		userService.remove(user);
	}

	@Test
	public void getByEmailReturnsUserIfOneFound() {
		List<User> fakeList = new ArrayList<User>();
		fakeList.add(new User());

		Mockito.when(userRepositoryMock.getByFilter(Mockito.any(UserFilter.class))).thenReturn(fakeList);

		User user = userService.getByEmail("John_Kross@xxx.com");
		assertNotNull(user);

		ArgumentCaptor<UserFilter> filterArgument = ArgumentCaptor.forClass(UserFilter.class);
		Mockito.verify(userRepositoryMock).getByFilter(filterArgument.capture());
		assertNotNull(filterArgument.getValue());
		assertEquals("John_Kross@xxx.com", filterArgument.getValue().getEmail());
	}

	@Test
	public void getByEmailReturnsNullIfNotFound() {
		List<User> emptyList = Collections.<User>emptyList();

		Mockito.when(userRepositoryMock.getByFilter(Mockito.any(UserFilter.class))).thenReturn(emptyList);

		User user = userService.getByEmail("John_Kross@xxx.com");
		assertNull(user);
	}

	@Test(expected=Exception.class)
	public void getByEmailThrowsExceptionIfMoreThanOneFound() {
		List<User> fakeList = new ArrayList<User>();
		fakeList.add(new User());
		fakeList.add(new User());

		Mockito.when(userRepositoryMock.getByFilter(Mockito.any(UserFilter.class))).thenReturn(fakeList);

		userService.getByEmail("John_Kross@xxx.com");
	}
}
