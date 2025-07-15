# 🌿 Gestion Association – Déploiement Kubernetes avec CI/CD GitLab et Argo CD

Ce projet est une application Java Spring Boot pour la gestion d'associations, déployée sur un cluster **Azure Kubernetes Service (AKS)** via une chaîne CI/CD automatisée avec **GitLab**, **Docker**, **Helm** et **Argo CD**.

---

## 🧩 Stack technique

| Outil/Service             | Rôle                                                 |
|---------------------------|------------------------------------------------------|
| Java 17 / Spring Boot     | Backend REST                                         |
| Maven                     | Build & packaging Java                               |
| Docker                    | Conteneurisation                                     |
| GitLab CI/CD              | Automatisation du build, Docker, déploiement Helm   |
| Helm                      | Templates de déploiement Kubernetes                  |
| Azure Kubernetes (AKS)    | Infrastructure Kubernetes                            |
| Azure CosmosDB PostgreSQL | Base de données relationnelle                        |
| Argo CD                   | GitOps pour déploiement continu                      |

---

## 📂 Structure du projet

```text
gestion-association/
├── .gitlab-ci.yml        # Pipeline CI/CD GitLab
├── Dockerfile            # Conteneurisation de l'app
├── src/                  # Code source Spring Boot
├── target/               # .jar généré par Maven
├── README.md             # Ce fichier

```
⚠️ Les fichiers Helm et manifestes Kubernetes doivent être placés dans un dossier séparé nommé assoc-manifests (idéalement dans un dépôt Git séparé). Ce dossier est utilisé par Argo CD pour surveiller et déployer automatiquement l'application sur le cluster AKS.



---

## ⚙️ Pipeline CI/CD – GitLab

### Étapes automatisées :
1. **Build Maven** : compile le projet et génère un `.jar` avec la commande ( mvn clean install) 
2. **Build & Push Docker** : construit une image Docker et la pousse dans le [GitLab Container Registry](https://docs.gitlab.com/ee/user/packages/container_registry/)
3. **Mise à jour Helm Chart** : modifie dynamiquement le `values.yaml` dans le dépôt `assoc-manifests` avec le nouveau tag d’image, déclenchant ainsi le déploiement via ArgoCD.

---

## 🐳 Dockerfile

```dockerfile
FROM maven:3.9.10-eclipse-temurin-17

ENV APP_NAME=gestion-association

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 9092

ENTRYPOINT ["java", "-jar", "app.jar"]
```


## 🚀 Déploiement GitOps avec Argo CD
Argo CD est connecté au dépôt assoc-manifests.

À chaque commit Git déclenché par le pipeline, Argo CD détecte le changement de tag dans le values.yaml et déploie automatiquement la nouvelle image sur AKS.



## 🔐 Connexion à la base de données Azure PostgreSQL
Les variables d'environnement sensibles (URL, utilisateur, mot de passe) sont injectées dans le pod via des Secrets Kubernetes définis dans le dépôt assoc-manifests :

```yaml
env:
  - name: SPRING_DATASOURCE_URL
    valueFrom:
      secretKeyRef:
        name: azure-postgres-secret
        key: SPRING_DATASOURCE_URL

```


## 🚀 Pour utiliser ce projet
-Cloner ce dépôt :
git clone https://gitlab.com/imaneabdel/association-emsi-v2.git
cd association-emsi-v2

-Compiler localement :
mvn clean package -DskipTests

-Configurer les variables GitLab CI :
CI_REGISTRY_USER
CI_REGISTRY_PASSWORD
SSH_PRIVATE_KEY (accès au repo assoc-manifests)

-Le pipeline se déclenche automatiquement après chaque push.

-Argo CD se charge de la synchronisation automatique avec AKS.



## 🎨 UI/UX – Interfaces Utilisateur
L'application Gestion Association propose plusieurs interfaces organisées pour une navigation fluide et intuitive :

