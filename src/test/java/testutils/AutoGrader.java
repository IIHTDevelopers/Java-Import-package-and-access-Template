package testutils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public class AutoGrader {

	// Test if the code demonstrates proper package creation, methods, and import
	// access
	public boolean testPackageAndMethods(String filePath) throws IOException {
		System.out.println("Starting testPackageAndMethods with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		File projectDir = new File(System.getProperty("user.dir"));
		if (!projectDir.exists() || !projectDir.isDirectory()) {
			System.out.println("Invalid directory path: " + projectDir.getAbsolutePath());
			return false;
		}

		Set<String> classes = new HashSet<>();
		Set<String> packages = new HashSet<>();
//		System.out.println("Listing all classes in the project directory:");
		listClassesAndPackages(projectDir, classes, packages); // Get all classes and packages in the directory

		// Print all unique packages
//		System.out.println("Detected packages:");
//		packages.forEach(System.out::println);

		// Print all unique classes
//		System.out.println("Detected classes:");
//		classes.forEach(System.out::println);

		// Proceed with checking other criteria if the required packages and classes are
		// found
		AtomicBoolean packageUtilityFound = new AtomicBoolean(false);
		AtomicBoolean packageAssessmentFound = new AtomicBoolean(false);
		AtomicBoolean mathOperationsClassFound = new AtomicBoolean(false);
		AtomicBoolean stringOperationsClassFound = new AtomicBoolean(false);
		AtomicBoolean mainMethodFound = new AtomicBoolean(false);
		AtomicBoolean mathOperationsReferenceFound = new AtomicBoolean(false);
		AtomicBoolean stringOperationsReferenceFound = new AtomicBoolean(false);

		// Check if the required packages exist
		if (packages.contains("com.yaksha.utility")) {
			packageUtilityFound.set(true);
		}
		if (packages.contains("com.yaksha.assignment")) {
			packageAssessmentFound.set(true);
		}

		// Ensure the required packages are found
		if (!packageUtilityFound.get()) {
			System.out.println("Error: Package 'com.yaksha.utility' not found.");
			return false;
		}
		if (!packageAssessmentFound.get()) {
			System.out.println("Error: Package 'com.yaksha.assignment' not found.");
			return false;
		}

		// Check if the required classes exist
		if (classes.contains("MathOperations")) {
			mathOperationsClassFound.set(true);
		}
		if (classes.contains("StringOperations")) {
			stringOperationsClassFound.set(true);
		}

		// Ensure the required classes are found
		if (!mathOperationsClassFound.get()) {
			System.out.println("Error: Class 'MathOperations' not found in package 'com.yaksha.utility'.");
			return false;
		}
		if (!stringOperationsClassFound.get()) {
			System.out.println("Error: Class 'StringOperations' not found in package 'com.yaksha.utility'.");
			return false;
		}

		// Read the Java file and create CompilationUnit to access the classes and
		// methods
		CompilationUnit cu = StaticJavaParser.parse(participantFile);

		// Now perform checks for references and method calls in main method
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;
//				System.out.println("Detected class: " + classDecl.getNameAsString());

				// Check for the main method in Main class
				if (classDecl.getNameAsString().equals("Main")) {
					classDecl.getMethods().forEach(method -> {
						if (method.getNameAsString().equals("main")) {
							mainMethodFound.set(true);
							System.out.println("Main method found in 'Main' class.");
							// Check if MathOperations and StringOperations references are created in main
							// method
							method.getBody().ifPresent(body -> {
								body.findAll(VariableDeclarationExpr.class).forEach(varDecl -> {
									// Check for creation of MathOperations reference
									varDecl.getVariables().forEach(variable -> {
										if (variable.getNameAsString().equals("mathOperations")
												&& variable.getType().asString().equals("MathOperations")) {
											mathOperationsReferenceFound.set(true);
											System.out.println(
													"Reference of type 'MathOperations' found in main method.");
										}
										// Check for creation of StringOperations reference
										if (variable.getNameAsString().equals("stringOperations")
												&& variable.getType().asString().equals("StringOperations")) {
											stringOperationsReferenceFound.set(true);
											System.out.println(
													"Reference of type 'StringOperations' found in main method.");
										}
									});
								});

								// Check for method calls on MathOperations and StringOperations references
								body.findAll(MethodCallExpr.class).forEach(callExpr -> {
									if (callExpr.getNameAsString().equals("add")
											&& mathOperationsReferenceFound.get()) {
										System.out
												.println("Method 'add' called on 'MathOperations' reference in main.");
									}
									if (callExpr.getNameAsString().equals("multiply")
											&& mathOperationsReferenceFound.get()) {
										System.out.println(
												"Method 'multiply' called on 'MathOperations' reference in main.");
									}
									if (callExpr.getNameAsString().equals("concatenate")
											&& stringOperationsReferenceFound.get()) {
										System.out.println(
												"Method 'concatenate' called on 'StringOperations' reference in main.");
									}
									if (callExpr.getNameAsString().equals("getLength")
											&& stringOperationsReferenceFound.get()) {
										System.out.println(
												"Method 'getLength' called on 'StringOperations' reference in main.");
									}
								});
							});
						}
					});
				}
			}
		}

		// Ensure the main method was found
		if (!mainMethodFound.get()) {
			System.out.println("Error: Main method not found.");
			return false;
		}

		// Ensure that references were created and methods were called
		if (!mathOperationsReferenceFound.get()) {
			System.out.println("Error: Reference with name mathOperations of type 'MathOperations' not created in main method.");
			return false;
		}

		if (!stringOperationsReferenceFound.get()) {
			System.out.println("Error: Reference with name stringOperations of type 'StringOperations' not created in main method.");
			return false;
		}

		System.out.println("Test passed: All checks passed successfully.");
		return true;
	}

	// Method to list all classes and packages recursively from the provided
	// directory
	private static void listClassesAndPackages(File dir, Set<String> classes, Set<String> packages) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
//				System.out.println("Entering directory: " + file.getAbsolutePath());
				listClassesAndPackages(file, classes, packages);
			} else if (file.getName().endsWith(".java")) {
//				System.out.println("Found Java file: " + file.getAbsolutePath());
				try {
					CompilationUnit cu = StaticJavaParser.parse(file);
					cu.getPackageDeclaration().ifPresent(pkg -> {
						packages.add(pkg.getNameAsString());
//						System.out.println("Detected package: " + pkg.getNameAsString());
					});
					cu.getTypes().forEach(type -> {
//						System.out.println("Detected class: " + type.getNameAsString());
						classes.add(type.getNameAsString());
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
