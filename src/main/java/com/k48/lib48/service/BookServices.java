package com.k48.lib48.service;

import com.k48.lib48.models.Book;
import com.k48.lib48.models.Category;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.CategoryRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookServices {

    private final BookRespositories bookRespo;
    private final CategoryRepositories categoryRepo;

    public BookServices(BookRespositories bookRespo, CategoryRepositories categoryRepo) {
        this.bookRespo = bookRespo;
        this.categoryRepo = categoryRepo;
    }

    public List<Book> getAllBooks() {
        return bookRespo.findAll();
    }

    public Book getBookId(long id) {
        return bookRespo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

    public Book getBooksByTitle(String title) {
        Book book = bookRespo.findByTitreIgnoreCase(title);
        if (book == null) {
            throw new NoSuchElementException("Book not found");
        }
        return book;
    }

    public List<Book> getBooksByAuthor(String author) {
        List<Book> books = bookRespo.findByAuteurIgnoreCase(author);
        if (books.isEmpty()) {
            throw new NoSuchElementException("Book not found");
        }
        return books;
    }

    public List<Book> getBooksByEditeur(String editeur) {
        List<Book> books = bookRespo.findByEditeurIgnoreCase(editeur);
        if (books.isEmpty()) {
            throw new NoSuchElementException("Book not found");
        }
        return books;
    }

    public List<Book> getBooksByEtatLivre(EtatLivre etatLivre) {
        List<Book> books = bookRespo.findByEtatLivre(etatLivre);
        if (books.isEmpty()) {
            throw new NoSuchElementException("Book not found");
        }
        return books;
    }

    public List<Book> getBooksByCategorieNom(String nomCategorie) {
        Category category = categoryRepo.findByNomIgnoreCase(nomCategorie);
        if (category == null) {
            throw new NoSuchElementException("Category not found");
        }
        return bookRespo.findByCategory(category);
    }

    public Book createBook(Book book) {
        // Validation titre obligatoire
        if (book.getTitre() == null || book.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("The Title cannot be null or empty");
        }

        // Gestion catégorie
        if (book.getCategory() == null) {
            throw new IllegalArgumentException("The Category must be specified");
        }

        Category category = book.getCategory();

        if (category.getId() != null && category.getId() > 0) {
            // Recherche catégorie existante par ID
            category = categoryRepo.findById(category.getId())
                    .orElseThrow(() -> new NoSuchElementException("Category not found with id: "));
        } else if (category.getNom() != null && !category.getNom().trim().isEmpty()) {
            // Pas d'id, recherche par nom puis création sinon
            Category existingCategory = categoryRepo.findByNomIgnoreCase(category.getNom().trim());
            if (existingCategory != null) {
                category = existingCategory;
            } else {
                Category newCategory = new Category();
                newCategory.setNom(category.getNom().trim());
                category = categoryRepo.save(newCategory);
            }
        } else {
            throw new IllegalArgumentException("Category must have either a valid id or a non-empty name");
        }

        book.setCategory(category);

        // Pour forcer création JPA (attention si id est primitif long => changer en Long dans Book)

        return bookRespo.save(book);
    }

    public Book updateBook(Book book) {
        Book existingBook = bookRespo.findById(book.getId())
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        existingBook.setTitre(book.getTitre());
        existingBook.setAuteur(book.getAuteur());
        existingBook.setEditeur(book.getEditeur());
        existingBook.setDisponible(book.isDisponible());
        existingBook.setEtatLivre(book.getEtatLivre());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setDatePublication(book.getDatePublication());

        if (book.getCategory() != null) {
            Long categoryId = book.getCategory().getId();
            Category category = categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
            existingBook.setCategory(category);
        }

        return bookRespo.save(existingBook);
    }

    public void deleteBook(long id) {
        if (!bookRespo.existsById(id)) {
            throw new NoSuchElementException("Book not found");
        }
        bookRespo.deleteById(id);
    }
}
