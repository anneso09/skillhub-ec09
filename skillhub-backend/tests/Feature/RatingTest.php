<?php

namespace Tests\Feature;

use Tests\TestCase;
use App\Models\Formation;
use App\Models\Enrollment;
use App\Models\Rating;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Http;

class RatingTest extends TestCase
{
    use RefreshDatabase;

    private function mockAuth(int $userId, string $role = 'apprenant'): array
    {
        Http::fake([
            '*/api/auth/validate' => Http::response([
                'email'  => 'test@test.com',
                'role'   => $role,
                'userId' => $userId,
            ], 200),
        ]);

        return ['Authorization' => 'Bearer fake_token'];
    }

    private function setupFormationEtInscription(int $apprenantId): Formation
    {
        User::create([
            'nom'      => 'Formateur',
            'prenom'   => 'Test',
            'email'    => 'formateur@test.com',
            'password' => 'password',
            'role'     => 'formateur',
        ]);

        $formation = Formation::create([
            'titre'        => 'Formation Test',
            'description'  => 'Description test',
            'categorie'    => 'Test',
            'niveau'       => 'Débutant',
            'formateur_id' => 1,
        ]);

        Enrollment::create([
            'utilisateur_id' => $apprenantId,
            'formation_id'   => $formation->id,
            'progression'    => 0,
        ]);

        return $formation;
    }

    /** @test */
    public function un_apprenant_inscrit_peut_noter_une_formation()
    {
        $headers   = $this->mockAuth(1);
        $formation = $this->setupFormationEtInscription(1);

        $response = $this->postJson(
            "/api/formations/{$formation->id}/noter",
            ['note' => 4, 'commentaire' => 'Très bonne formation'],
            $headers
        );

        $response->assertStatus(201);
        $response->assertJsonFragment(['note' => 4]);
        $this->assertDatabaseHas('ratings', [
            'formation_id' => $formation->id,
            'apprenant_id' => 1,
            'note'         => 4,
        ]);
    }

    /** @test */
    public function un_apprenant_non_inscrit_ne_peut_pas_noter()
    {
        $headers = $this->mockAuth(1);

        User::create([
            'nom'      => 'Formateur',
            'prenom'   => 'Test',
            'email'    => 'formateur@test.com',
            'password' => 'password',
            'role'     => 'formateur',
        ]);

        $formation = Formation::create([
            'titre'        => 'Formation Test',
            'description'  => 'Description',
            'categorie'    => 'Test',
            'niveau'       => 'Débutant',
            'formateur_id' => 1,
        ]);

        $response = $this->postJson(
            "/api/formations/{$formation->id}/noter",
            ['note' => 4],
            $headers
        );

        $response->assertStatus(403);
    }

    /** @test */
    public function une_note_hors_intervalle_retourne_400()
    {
        $headers   = $this->mockAuth(1);
        $formation = $this->setupFormationEtInscription(1);

        $response = $this->postJson(
            "/api/formations/{$formation->id}/noter",
            ['note' => 6],
            $headers
        );

        $response->assertStatus(400);
    }

    /** @test */
    public function une_note_inferieure_a_1_retourne_400()
    {
        $headers   = $this->mockAuth(1);
        $formation = $this->setupFormationEtInscription(1);

        $response = $this->postJson(
            "/api/formations/{$formation->id}/noter",
            ['note' => 0],
            $headers
        );

        $response->assertStatus(400);
    }

    /** @test */
    public function un_apprenant_ne_peut_pas_noter_deux_fois()
    {
        $headers   = $this->mockAuth(1);
        $formation = $this->setupFormationEtInscription(1);

        $this->postJson(
            "/api/formations/{$formation->id}/noter",
            ['note' => 4],
            $headers
        );

        $response = $this->postJson(
            "/api/formations/{$formation->id}/noter",
            ['note' => 5],
            $headers
        );

        $response->assertStatus(400);
        $response->assertJsonFragment(['message' => 'Vous avez déjà noté cette formation.']);
    }

    /** @test */
    public function le_get_formation_retourne_note_moyenne_et_nombre_avis()
    {
        $formation = $this->setupFormationEtInscription(1);

        Rating::create([
            'formation_id' => $formation->id,
            'apprenant_id' => 1,
            'note'         => 4,
            'commentaire'  => 'Bien',
        ]);

        $response = $this->getJson("/api/formations/{$formation->id}");

        $response->assertStatus(200);
        $response->assertJsonFragment([
            'note_moyenne' => 4.0,
            'nombre_avis'  => 1,
        ]);
    }
}